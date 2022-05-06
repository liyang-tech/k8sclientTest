package com.k8sclient.demo.authenticators;

import com.k8sclient.demo.k8sutils.KubeConfig;
import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientAssertion;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author liyang(leonasli)
 * @className AzureActiveDirectoryAuthenticator
 * @description TODO
 * @create 2022/4/27 13:55
 **/
public class AzureActiveDirectoryAuthenticator implements Authenticator {
    private static final String ACCESS_TOKEN = "access-token";
    private static final String EXPIRES_ON = "expires-on";
    private static final String TENANT_ID = "tenant-id";
    private static final String CLIENT_ID = "client-id";
    private static final String REFRESH_TOKEN = "refresh-token";

    public AzureActiveDirectoryAuthenticator() {
    }

    public String getName() {
        return "azure";
    }

    public String getToken(Map<String, Object> config) {
        return (String)config.get("access-token");
    }

    public boolean isExpired(Map<String, Object> config) {
        String expiresOn = (String)config.get("expires-on");
        Date expiry = new Date(Long.parseLong(expiresOn) * 1000L);
        return expiry.compareTo(new Date()) <= 0;
    }

    public Map<String, Object> refresh(Map<String, Object> config) {
        String cloud = "https://login.microsoftonline.com";
        String tenantId = (String)config.get("tenant-id");
        String authority = cloud + "/" + tenantId;
        String clientId = (String)config.get("client-id");
        String refreshToken = (String)config.get("refresh-token");

        try {
            AuthenticationContext context = new AuthenticationContext(authority, true, Executors.newSingleThreadExecutor());
            Future<AuthenticationResult> resultFuture = context.acquireTokenByRefreshToken(refreshToken, clientId, (ClientAssertion)null, (AuthenticationCallback)null);
            AuthenticationResult result = (AuthenticationResult)resultFuture.get();
            config.put("access-token", result.getAccessToken());
            config.put("refresh-token", result.getRefreshToken());
            return config;
        } catch (MalformedURLException | ExecutionException | InterruptedException var10) {
            throw new RuntimeException(var10);
        }
    }

    static {
        KubeConfig.registerAuthenticator(new AzureActiveDirectoryAuthenticator());
    }
}