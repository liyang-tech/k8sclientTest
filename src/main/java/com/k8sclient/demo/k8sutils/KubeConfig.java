package com.k8sclient.demo.k8sutils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.k8sclient.demo.authenticators.Authenticator;
import com.k8sclient.demo.authenticators.AzureActiveDirectoryAuthenticator;
import com.k8sclient.demo.authenticators.GCPAuthenticator;
import com.k8sclient.demo.authenticators.OpenIDConnectAuthenticator;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * @author liyang(leonasli)
 * @className KubeConfig
 * @description TODO
 * @create 2022/4/27 13:51
 **/
public class KubeConfig {
    private static final Logger log = LoggerFactory.getLogger(KubeConfig.class);
    public static final String ENV_HOME = "HOME";
    public static final String KUBEDIR = ".kube";
    public static final String KUBECONFIG = "config";
    private static Map<String, Authenticator> authenticators = new HashMap();
    private ArrayList<Object> clusters;
    private ArrayList<Object> contexts;
    private ArrayList<Object> users;
    String currentContextName;
    Map<String, Object> currentContext;
    Map<String, Object> currentCluster;
    Map<String, Object> currentUser;
    String currentNamespace;
    Object preferences;
    ConfigPersister persister;
    private File file;

    public static void registerAuthenticator(Authenticator auth) {
        synchronized(authenticators) {
            authenticators.put(auth.getName(), auth);
        }
    }

    public static KubeConfig loadKubeConfig(Reader input) {
        Yaml yaml = new Yaml(new SafeConstructor());
        Object config = yaml.load(input);
        Map<String, Object> configMap = (Map)config;
        String currentContext = (String)configMap.get("current-context");
        ArrayList<Object> contexts = (ArrayList)configMap.get("contexts");
        ArrayList<Object> clusters = (ArrayList)configMap.get("clusters");
        ArrayList<Object> users = (ArrayList)configMap.get("users");
        Object preferences = configMap.get("preferences");
        KubeConfig kubeConfig = new KubeConfig(contexts, clusters, users);
        kubeConfig.setContext(currentContext);
        kubeConfig.setPreferences(preferences);
        return kubeConfig;
    }

    public KubeConfig(ArrayList<Object> contexts, ArrayList<Object> clusters, ArrayList<Object> users) {
        this.contexts = contexts;
        this.clusters = clusters;
        this.users = users;
    }

    public String getCurrentContext() {
        return this.currentContextName;
    }

    public boolean setContext(String context) {
        if (context == null) {
            return false;
        } else {
            this.currentContextName = context;
            this.currentCluster = null;
            this.currentUser = null;
            Map<String, Object> ctx = findObject(this.contexts, context);
            if (ctx == null) {
                return false;
            } else {
                this.currentContext = (Map)ctx.get("context");
                if (this.currentContext == null) {
                    return false;
                } else {
                    String cluster = (String)this.currentContext.get("cluster");
                    String user = (String)this.currentContext.get("user");
                    this.currentNamespace = (String)this.currentContext.get("namespace");
                    Map obj;
                    if (cluster != null) {
                        obj = findObject(this.clusters, cluster);
                        if (obj != null) {
                            this.currentCluster = (Map)obj.get("cluster");
                        }
                    }

                    if (user != null) {
                        obj = findObject(this.users, user);
                        if (obj != null) {
                            this.currentUser = (Map)obj.get("user");
                        }
                    }

                    return true;
                }
            }
        }
    }

    public ArrayList<Object> getContexts() {
        return this.contexts;
    }

    public ArrayList<Object> getClusters() {
        return this.clusters;
    }

    public ArrayList<Object> getUsers() {
        return this.users;
    }

    public String getNamespace() {
        return this.currentNamespace;
    }

    public Object getPreferences() {
        return this.preferences;
    }

    public String getServer() {
        return getData(this.currentCluster, "server");
    }

    public String getCertificateAuthorityData() {
        return getData(this.currentCluster, "certificate-authority-data");
    }

    public String getCertificateAuthorityFile() {
        return getData(this.currentCluster, "certificate-authority");
    }

    public String getClientCertificateFile() {
        return getData(this.currentUser, "client-certificate");
    }

    public String getClientCertificateData() {
        return getData(this.currentUser, "client-certificate-data");
    }

    public String getClientKeyFile() {
        return getData(this.currentUser, "client-key");
    }

    public String getClientKeyData() {
        return getData(this.currentUser, "client-key-data");
    }

    public String getUsername() {
        return getData(this.currentUser, "username");
    }

    public String getPassword() {
        return getData(this.currentUser, "password");
    }

    public String getAccessToken() {
        if (this.currentUser == null) {
            return null;
        } else {
            Object authProvider = this.currentUser.get("auth-provider");
            if (authProvider != null) {
                Map<String, Object> authProviderMap = (Map)authProvider;
                Map<String, Object> authConfig = (Map)authProviderMap.get("config");
                if (authConfig != null) {
                    String name = (String)authProviderMap.get("name");
                    Authenticator auth = (Authenticator)authenticators.get(name);
                    if (auth != null) {
                        if (auth.isExpired(authConfig)) {
                            authConfig = auth.refresh(authConfig);
                            if (this.persister != null) {
                                try {
                                    this.persister.save(this.contexts, this.clusters, this.users, this.preferences, this.currentContextName);
                                } catch (IOException var7) {
                                    log.error("Failed to persist new token", var7);
                                }
                            }
                        }

                        return auth.getToken(authConfig);
                    }

                    log.error("Unknown auth provider: " + name);
                }
            }

            String tokenViaExecCredential = this.tokenViaExecCredential((Map)this.currentUser.get("exec"));
            if (tokenViaExecCredential != null) {
                return tokenViaExecCredential;
            } else if (this.currentUser.containsKey("token")) {
                return (String)this.currentUser.get("token");
            } else {
                if (this.currentUser.containsKey("tokenFile")) {
                    String tokenFile = (String)this.currentUser.get("tokenFile");

                    try {
                        byte[] data = Files.readAllBytes(FileSystems.getDefault().getPath(tokenFile));
                        return new String(data, StandardCharsets.UTF_8);
                    } catch (IOException var8) {
                        log.error("Failed to read token file", var8);
                    }
                }

                return null;
            }
        }
    }

    private String tokenViaExecCredential(Map<String, Object> execMap) {
        if (execMap == null) {
            return null;
        } else {
            String apiVersion = (String)execMap.get("apiVersion");
            if (!"client.authentication.k8s.io/v1beta1".equals(apiVersion) && !"client.authentication.k8s.io/v1alpha1".equals(apiVersion)) {
                log.error("Unrecognized user.exec.apiVersion: {}", apiVersion);
                return null;
            } else {
                String command = (String)execMap.get("command");
                JsonElement root = this.runExec(command, (List)execMap.get("args"), (List)execMap.get("env"));
                if (root == null) {
                    return null;
                } else if (!"ExecCredential".equals(root.getAsJsonObject().get("kind").getAsString())) {
                    log.error("Unrecognized kind in response");
                    return null;
                } else if (!apiVersion.equals(root.getAsJsonObject().get("apiVersion").getAsString())) {
                    log.error("Mismatched apiVersion in response");
                    return null;
                } else {
                    JsonObject status = root.getAsJsonObject().get("status").getAsJsonObject();
                    JsonElement token = status.get("token");
                    if (token == null) {
                        log.warn("No token produced by {}", command);
                        return null;
                    } else {
                        log.debug("Obtained a token from {}", command);
                        return token.getAsString();
                    }
                }
            }
        }
    }

    private JsonElement runExec(String command, List<String> args, List<Map<String, String>> env) {
        List<String> argv = new ArrayList();
        if (!command.contains("/") && !command.contains("\\")) {
            argv.add(command);
        } else {
            Path resolvedCommand = this.file.toPath().getParent().resolve(command).normalize();
            if (!Files.exists(resolvedCommand, new LinkOption[0])) {
                log.error("No such file: {}", resolvedCommand);
                return null;
            }

            log.debug("Resolved {} to {}", command, resolvedCommand);
            argv.add(resolvedCommand.toString());
        }

        if (args != null) {
            argv.addAll(args);
        }

        ProcessBuilder pb = new ProcessBuilder(argv);
        Object root;
        if (env != null) {
            Iterator var6 = env.iterator();

            while(var6.hasNext()) {
                root = (Map)var6.next();
                pb.environment().put(((Map)root).get("name").toString(), ((Map)root).get("value").toString());
            }
        }

        pb.redirectError(Redirect.INHERIT);

        try {
            Process proc = pb.start();

            try {
                InputStream is = proc.getInputStream();
                Throwable var9 = null;

                try {
                    Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
                    Throwable var11 = null;

                    try {
                        root = JsonParser.parseReader(r);
                    } catch (Throwable var38) {
                        var11 = var38;
                        throw var38;
                    } finally {
                        if (r != null) {
                            if (var11 != null) {
                                try {
                                    r.close();
                                } catch (Throwable var37) {
                                    var11.addSuppressed(var37);
                                }
                            } else {
                                r.close();
                            }
                        }

                    }
                } catch (Throwable var40) {
                    var9 = var40;
                    throw var40;
                } finally {
                    if (is != null) {
                        if (var9 != null) {
                            try {
                                is.close();
                            } catch (Throwable var36) {
                                var9.addSuppressed(var36);
                            }
                        } else {
                            is.close();
                        }
                    }

                }
            } catch (JsonParseException var42) {
                log.error("Failed to parse output of " + command, var42);
                return null;
            }

            int r = proc.waitFor();
            if (r != 0) {
                log.error("{} failed with exit code {}", command, r);
                return null;
            } else {
                return (JsonElement)root;
            }
        } catch (InterruptedException | IOException var43) {
            log.error("Failed to run " + command, var43);
            return null;
        }
    }

    public boolean verifySSL() {
        if (this.currentCluster == null) {
            return false;
        } else if (this.currentCluster.containsKey("insecure-skip-tls-verify")) {
            return !(Boolean)this.currentCluster.get("insecure-skip-tls-verify");
        } else {
            return true;
        }
    }

    public void setPersistConfig(ConfigPersister persister) {
        this.persister = persister;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setPreferences(Object preferences) {
        this.preferences = preferences;
    }

    private static String getData(Map<String, Object> obj, String key) {
        return obj == null ? null : (String)obj.get(key);
    }

    private static Map<String, Object> findObject(ArrayList<Object> list, String name) {
        if (list == null) {
            return null;
        } else {
            Iterator var2 = list.iterator();

            Map map;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                Object obj = var2.next();
                map = (Map)obj;
            } while(!name.equals(map.get("name")));

            return map;
        }
    }

    public byte[] getDataOrFileRelative(String data, String path) throws IOException {
        String resolvedPath = path;
        if (path != null && this.file != null) {
            resolvedPath = this.file.toPath().getParent().resolve(path).normalize().toString();
        }

        return getDataOrFile(data, resolvedPath);
    }

    private static byte[] getDataOrFile(String data, String file) throws IOException {
        if (data != null) {
            return Base64.decodeBase64(data);
        } else {
            return file != null ? Files.readAllBytes(Paths.get(file)) : null;
        }
    }

    static {
        registerAuthenticator(new GCPAuthenticator());
        registerAuthenticator(new AzureActiveDirectoryAuthenticator());
        registerAuthenticator(new OpenIDConnectAuthenticator());
    }
}
