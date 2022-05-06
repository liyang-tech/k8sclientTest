package com.k8sclient.demo.authenticators;

import com.alibaba.fastjson.JSONObject;
import com.k8sclient.demo.k8sutils.KubeConfig;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;

/**
 * @author liyang(leonasli)
 * @className OpenIDConnectAuthenticator
 * @description TODO
 * @create 2022/4/27 13:56
 **/
public class OpenIDConnectAuthenticator implements Authenticator {
    public static final String OIDC_ID_TOKEN = "id-token";
    public static final String OIDC_ISSUER = "idp-issuer-url";
    public static final String OIDC_REFRESH_TOKEN = "refresh-token";
    public static final String OIDC_CLIENT_ID = "client-id";
    public static final String OIDC_CLIENT_SECRET = "client-secret";
    public static final String OIDC_IDP_CERT_DATA = "idp-certificate-authority-data";

    public OpenIDConnectAuthenticator() {
    }

    public String getName() {
        return "oidc";
    }

    public String getToken(Map<String, Object> config) {
        return (String)config.get("id-token");
    }

    public boolean isExpired(Map<String, Object> config) {
        String idToken = (String)config.get("id-token");
        if (idToken == null) {
            return true;
        } else {
            JsonWebSignature jws = new JsonWebSignature();

            try {
                jws.setCompactSerialization(idToken);
                String jwt = jws.getUnverifiedPayload();
                JwtClaims claims = JwtClaims.parse(jwt);
                return claims.getExpirationTime() == null || NumericDate.now().isOnOrAfter(claims.getExpirationTime());
            } catch (InvalidJwtException | MalformedClaimException | JoseException var6) {
                throw new RuntimeException(var6);
            }
        }
    }

    public Map<String, Object> refresh(Map<String, Object> config) {
        String issuer = (String)config.get("idp-issuer-url");
        String clientId = (String)config.get("client-id");
        String refreshToken = (String)config.get("refresh-token");
        String clientSecret = (String)config.getOrDefault("client-secret", "");
        String idpCert = (String)config.get("idp-certificate-authority-data");
        SSLContext sslContext = null;
        String pemCert;
        if (idpCert != null) {
            pemCert = new String(Base64.getDecoder().decode(idpCert));
            String alias = "doenotmatter";

            try {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load((InputStream)null, alias.toCharArray());
                ByteArrayInputStream bais = new ByteArrayInputStream(pemCert.getBytes(StandardCharsets.UTF_8));
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> c = cf.generateCertificates(bais);
                int j = 0;

                for(Iterator var15 = c.iterator(); var15.hasNext(); ++j) {
                    Certificate certificate = (Certificate)var15.next();
                    ks.setCertificateEntry(alias + "-" + j, certificate);
                }

                TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
                tmf.init(ks);
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init((KeyManager[])null, tmf.getTrustManagers(), new SecureRandom());
            } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException | KeyStoreException var17) {
                throw new RuntimeException("Could not import idp certificate", var17);
            }
        }

        pemCert = this.loadTokenURL(issuer, sslContext);
        JSONObject response = this.refreshOidcToken(clientId, refreshToken, clientSecret, sslContext, pemCert);
        config.put("id-token", response.get("id_token"));
        config.put("refresh-token", response.get("refresh_token"));
        return config;
    }

    private JSONObject refreshOidcToken(String clientId, String refreshToken, String clientSecret, SSLContext sslContext, String tokenURL) {
        try {
            URL tokenEndpoint = new URL(tokenURL);
            HttpsURLConnection https = (HttpsURLConnection)tokenEndpoint.openConnection();
            https.setRequestMethod("POST");
            if (sslContext != null) {
                https.setSSLSocketFactory(sslContext.getSocketFactory());
            }

            String credentials = Base64.getEncoder().encodeToString((clientId + ':' + clientSecret).getBytes(StandardCharsets.UTF_8));
            https.setRequestProperty("Authorization", "Basic " + credentials);
            https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            https.setDoOutput(true);
            String urlData = "refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8") + "&grant_type=refresh_token";
            OutputStream ou = https.getOutputStream();
            ou.write(urlData.getBytes(StandardCharsets.UTF_8));
            ou.flush();
            ou.close();
            int code = https.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Invalid response code for token retrieval - " + code);
            } else {
                Scanner scanner = new Scanner(https.getInputStream(), StandardCharsets.UTF_8.name());
                String json = scanner.useDelimiter("\\A").next();
                return (JSONObject)(new JSONParser()).parse(json);
            }
        } catch (Throwable var14) {
            throw new RuntimeException("Could not refresh token", var14);
        }
    }

    private String loadTokenURL(String issuer, SSLContext sslContext) {
        StringBuilder wellKnownUrl = new StringBuilder();
        wellKnownUrl.append(issuer);
        if (!issuer.endsWith("/")) {
            wellKnownUrl.append("/");
        }

        wellKnownUrl.append(".well-known/openid-configuration");

        try {
            URL wellKnown = new URL(wellKnownUrl.toString());
            HttpsURLConnection https = (HttpsURLConnection)wellKnown.openConnection();
            https.setRequestMethod("GET");
            if (sslContext != null) {
                https.setSSLSocketFactory(sslContext.getSocketFactory());
            }

            https.setUseCaches(false);
            int code = https.getResponseCode();
            if (code != 200) {
                throw new RuntimeException("Invalid response code for issuer - " + code);
            } else {
                Scanner scanner = new Scanner(https.getInputStream(), StandardCharsets.UTF_8.name());
                String json = scanner.useDelimiter("\\A").next();
                JSONObject wellKnownJson = (JSONObject)(new JSONParser()).parse(json);
                return (String)wellKnownJson.get("token_endpoint");
            }
        } catch (ParseException | IOException var10) {
            throw new RuntimeException("Could not refresh", var10);
        }
    }

    static {
        KubeConfig.registerAuthenticator(new OpenIDConnectAuthenticator());
    }
}
