//package com.k8sclient.demo.k8sutils;
//
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.openapi.ApiException;
//import io.kubernetes.client.openapi.models.V1CertificateSigningRequest;
//import io.kubernetes.client.util.CSRUtils;
//import io.kubernetes.client.util.FilePersister;
//import io.kubernetes.client.util.KubeConfig;
//import io.kubernetes.client.util.SSLUtils;
//import io.kubernetes.client.util.credentials.*;
//import io.kubernetes.client.util.exception.CSRNotApprovedException;
//import okhttp3.Protocol;
//import org.apache.commons.io.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.PrivateKey;
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author liyang(leonasli)
// * @className ClientBuilder
// * @description TODO
// * @create 2022/4/27 15:04
// **/
//public class ClientBuilder {
//    private static final Logger log = LoggerFactory.getLogger(io.kubernetes.client.util.ClientBuilder.class);
//    private String basePath = "http://localhost:8080";
//    private byte[] caCertBytes = null;
//    private boolean verifyingSsl = true;
//    private Authentication authentication;
//    private String keyStorePassphrase;
//    private List<Protocol> protocols;
//    private Duration readTimeout;
//    private Duration pingInterval;
//
//    public ClientBuilder() {
//        this.protocols = Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1);
//        this.readTimeout = Duration.ZERO;
//        this.pingInterval = Duration.ofMinutes(1L);
//    }
//
//    public static ApiClient defaultClient() throws IOException {
//        return standard().build();
//    }
//
//    public static io.kubernetes.client.util.ClientBuilder standard() throws IOException {
//        return standard(true);
//    }
//
//    public static io.kubernetes.client.util.ClientBuilder standard(boolean persistConfig) throws IOException {
//        File kubeConfig = findConfigFromEnv();
//        io.kubernetes.client.util.ClientBuilder clientBuilderEnv = getClientBuilder(persistConfig, kubeConfig);
//        if (clientBuilderEnv != null) {
//            return clientBuilderEnv;
//        } else {
//            File config = findConfigInHomeDir();
//            io.kubernetes.client.util.ClientBuilder clientBuilderHomeDir = getClientBuilder(persistConfig, config);
//            if (clientBuilderHomeDir != null) {
//                return clientBuilderHomeDir;
//            } else {
//                File clusterCa = new File("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt");
//                return clusterCa.exists() ? cluster() : new io.kubernetes.client.util.ClientBuilder();
//            }
//        }
//    }
//
//    private static io.kubernetes.client.util.ClientBuilder getClientBuilder(boolean persistConfig, File kubeConfig) throws IOException {
//        if (kubeConfig != null) {
//            BufferedReader kubeConfigReader = new BufferedReader(new InputStreamReader(new FileInputStream(kubeConfig), StandardCharsets.UTF_8.name()));
//            Throwable var3 = null;
//
//            io.kubernetes.client.util.ClientBuilder var5;
//            try {
//                io.kubernetes.client.util.KubeConfig kc = io.kubernetes.client.util.KubeConfig.loadKubeConfig(kubeConfigReader);
//                if (persistConfig) {
//                    kc.setPersistConfig(new FilePersister(kubeConfig));
//                }
//
//                kc.setFile(kubeConfig);
//                var5 = kubeconfig(kc);
//            } catch (Throwable var14) {
//                var3 = var14;
//                throw var14;
//            } finally {
//                if (kubeConfigReader != null) {
//                    if (var3 != null) {
//                        try {
//                            kubeConfigReader.close();
//                        } catch (Throwable var13) {
//                            var3.addSuppressed(var13);
//                        }
//                    } else {
//                        kubeConfigReader.close();
//                    }
//                }
//
//            }
//
//            return var5;
//        } else {
//            return null;
//        }
//    }
//
//    private static File findConfigFromEnv() {
//        io.kubernetes.client.util.ClientBuilder.KubeConfigEnvParser kubeConfigEnvParser = new io.kubernetes.client.util.ClientBuilder.KubeConfigEnvParser();
//        String kubeConfigPath = kubeConfigEnvParser.parseKubeConfigPath(System.getenv("KUBECONFIG"));
//        if (kubeConfigPath == null) {
//            return null;
//        } else {
//            File kubeConfig = new File(kubeConfigPath);
//            if (kubeConfig.exists()) {
//                return kubeConfig;
//            } else {
//                log.debug("Could not find file specified in $KUBECONFIG");
//                return null;
//            }
//        }
//    }
//
//    private static File findHomeDir() {
//        String envHome = System.getenv("HOME");
//        if (envHome != null && envHome.length() > 0) {
//            File config = new File(envHome);
//            if (config.exists()) {
//                return config;
//            }
//        }
//
//        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
//            String homeDrive = System.getenv("HOMEDRIVE");
//            String homePath = System.getenv("HOMEPATH");
//            if (homeDrive != null && homeDrive.length() > 0 && homePath != null && homePath.length() > 0) {
//                File homeDir = new File(new File(homeDrive), homePath);
//                if (homeDir.exists()) {
//                    return homeDir;
//                }
//            }
//
//            String userProfile = System.getenv("USERPROFILE");
//            if (userProfile != null && userProfile.length() > 0) {
//                File profileDir = new File(userProfile);
//                if (profileDir.exists()) {
//                    return profileDir;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private static File findConfigInHomeDir() {
//        File homeDir = findHomeDir();
//        if (homeDir != null) {
//            File config = new File(new File(homeDir, ".kube"), "config");
//            if (config.exists()) {
//                return config;
//            }
//        }
//
//        log.debug("Could not find ~/.kube/config");
//        return null;
//    }
//
//    public static io.kubernetes.client.util.ClientBuilder oldCluster() throws IOException {
//        io.kubernetes.client.util.ClientBuilder builder = new io.kubernetes.client.util.ClientBuilder();
//        String host = System.getenv("KUBERNETES_SERVICE_HOST");
//        String port = System.getenv("KUBERNETES_SERVICE_PORT");
//        builder.setBasePath(host, port);
//        String token = new String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token")), Charset.defaultCharset());
//        builder.setCertificateAuthority(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt")));
//        builder.setAuthentication(new AccessTokenAuthentication(token));
//        return builder;
//    }
//
//    public static io.kubernetes.client.util.ClientBuilder cluster() throws IOException {
//        io.kubernetes.client.util.ClientBuilder builder = new io.kubernetes.client.util.ClientBuilder();
//        String host = System.getenv("KUBERNETES_SERVICE_HOST");
//        String port = System.getenv("KUBERNETES_SERVICE_PORT");
//        builder.setBasePath(host, port);
//        builder.setCertificateAuthority(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt")));
//        builder.setAuthentication(new TokenFileAuthentication("/var/run/secrets/kubernetes.io/serviceaccount/token"));
//        return builder;
//    }
//
//    protected io.kubernetes.client.util.ClientBuilder setBasePath(String host, String port) {
//        try {
//            Integer iPort = Integer.valueOf(port);
//            URI uri = new URI("https", (String)null, host, iPort, (String)null, (String)null, (String)null);
//            this.setBasePath(uri.toString());
//            return this;
//        } catch (URISyntaxException | NumberFormatException var5) {
//            throw new IllegalStateException(var5);
//        }
//    }
//
//    public static io.kubernetes.client.util.ClientBuilder kubeconfig(io.kubernetes.client.util.KubeConfig config) throws IOException {
//        io.kubernetes.client.util.ClientBuilder builder = new io.kubernetes.client.util.ClientBuilder();
//        String server = config.getServer();
//        if (!server.contains("://")) {
//            if (server.contains(":443")) {
//                server = "https://" + server;
//            } else {
//                server = "http://" + server;
//            }
//        }
//
//        byte[] caBytes = config.getDataOrFileRelative(config.getCertificateAuthorityData(), config.getCertificateAuthorityFile());
//        if (caBytes != null) {
//            builder.setCertificateAuthority(caBytes);
//        }
//
//        builder.setVerifyingSsl(config.verifySSL());
//        builder.setBasePath(server);
//        builder.setAuthentication(new KubeconfigAuthentication(config));
//        return builder;
//    }
//
//    public static ApiClient fromCertificateSigningRequest(KubeConfig bootstrapKubeConfig, PrivateKey privateKey, V1CertificateSigningRequest csr) throws IOException, CSRNotApprovedException, ApiException {
//        ApiClient bootstrapApiClient = kubeconfig(bootstrapKubeConfig).build();
//        return fromCertificateSigningRequest(bootstrapApiClient, privateKey, csr);
//    }
//
//    public static ApiClient fromCertificateSigningRequest(ApiClient bootstrapApiClient, PrivateKey privateKey, V1CertificateSigningRequest csr) throws IOException, CSRNotApprovedException, ApiException {
//        byte[] certificateData = CSRUtils.createAndWaitUntilCertificateSigned(bootstrapApiClient, csr);
//        InputStream is = bootstrapApiClient.getSslCaCert();
//        is.reset();
//        io.kubernetes.client.util.ClientBuilder newBuilder = new io.kubernetes.client.util.ClientBuilder();
//        newBuilder.setAuthentication(new ClientCertificateAuthentication(certificateData, SSLUtils.dumpKey(privateKey)));
//        newBuilder.setBasePath(bootstrapApiClient.getBasePath());
//        newBuilder.setVerifyingSsl(bootstrapApiClient.isVerifyingSsl());
//        newBuilder.setCertificateAuthority(IOUtils.toByteArray(is));
//        return newBuilder.build();
//    }
//
//    public String getBasePath() {
//        return this.basePath;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setBasePath(String basePath) {
//        this.basePath = basePath;
//        return this;
//    }
//
//    public Authentication getAuthentication() {
//        return this.authentication;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setAuthentication(Authentication authentication) {
//        this.authentication = authentication;
//        return this;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setCertificateAuthority(byte[] caCertBytes) {
//        this.caCertBytes = caCertBytes;
//        return this;
//    }
//
//    public boolean isVerifyingSsl() {
//        return this.verifyingSsl;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setVerifyingSsl(boolean verifyingSsl) {
//        this.verifyingSsl = verifyingSsl;
//        return this;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setProtocols(List<Protocol> protocols) {
//        this.protocols = protocols;
//        return this;
//    }
//
//    public List<Protocol> getProtocols() {
//        return this.protocols;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setReadTimeout(Duration readTimeout) {
//        this.readTimeout = readTimeout;
//        return this;
//    }
//
//    public Duration getReadTimeout() {
//        return this.readTimeout;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setPingInterval(Duration pingInterval) {
//        this.pingInterval = pingInterval;
//        return this;
//    }
//
//    public Duration getPingInterval() {
//        return this.pingInterval;
//    }
//
//    public String getKeyStorePassphrase() {
//        return this.keyStorePassphrase;
//    }
//
//    public io.kubernetes.client.util.ClientBuilder setKeyStorePassphrase(String keyStorePassphrase) {
//        this.keyStorePassphrase = keyStorePassphrase;
//        return this;
//    }
//
//    public ApiClient build() {
//        ApiClient client = new ApiClient();
//        client.setHttpClient(client.getHttpClient().newBuilder().protocols(this.protocols).readTimeout(this.readTimeout).pingInterval(this.pingInterval).build());
//        if (this.basePath != null) {
//            if (this.basePath.endsWith("/")) {
//                this.basePath = this.basePath.substring(0, this.basePath.length() - 1);
//            }
//
//            client.setBasePath(this.basePath);
//        }
//
//        client.setVerifyingSsl(this.verifyingSsl);
//        if (this.authentication != null) {
//            if (StringUtils.isNotEmpty(this.keyStorePassphrase) && this.authentication instanceof KubeconfigAuthentication && ((KubeconfigAuthentication)this.authentication).getDelegateAuthentication() instanceof ClientCertificateAuthentication) {
//                ((ClientCertificateAuthentication)((ClientCertificateAuthentication)((KubeconfigAuthentication)this.authentication).getDelegateAuthentication())).setPassphrase(this.keyStorePassphrase);
//            }
//
//            this.authentication.provide(client);
//        }
//
//        if (this.caCertBytes != null) {
//            client.setSslCaCert(new ByteArrayInputStream(this.caCertBytes));
//        }
//
//        return client;
//    }
//
//    private static class KubeConfigEnvParser {
//        private KubeConfigEnvParser() {
//        }
//
//        private String parseKubeConfigPath(String kubeConfigEnv) {
//            if (kubeConfigEnv == null) {
//                return null;
//            } else {
//                String[] filePaths = kubeConfigEnv.split(File.pathSeparator);
//                String kubeConfigPath = filePaths[0];
//                if (filePaths.length > 1) {
//                    io.kubernetes.client.util.ClientBuilder.log.warn("Found multiple kubeconfigs files, $KUBECONFIG: " + kubeConfigEnv + " using first: {}", kubeConfigPath);
//                }
//
//                return kubeConfigPath;
//            }
//        }
//    }
//}
