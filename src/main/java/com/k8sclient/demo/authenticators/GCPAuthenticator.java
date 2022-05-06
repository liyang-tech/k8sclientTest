package com.k8sclient.demo.authenticators;

import com.k8sclient.demo.k8sutils.KubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * @author liyang(leonasli)
 * @className GCPAuthenticator
 * @description TODO
 * @create 2022/4/27 13:55
 **/
public class GCPAuthenticator implements Authenticator {
    private static final Logger log;

    public GCPAuthenticator() {
    }

    public String getName() {
        return "gcp";
    }

    public String getToken(Map<String, Object> config) {
        return (String)config.get("access-token");
    }

    public boolean isExpired(Map<String, Object> config) {
        Object expiryObj = config.get("expiry");
        Instant expiry = null;
        if (expiryObj instanceof Date) {
            expiry = ((Date)expiryObj).toInstant();
        } else if (expiryObj instanceof Instant) {
            expiry = (Instant)expiryObj;
        } else {
            if (!(expiryObj instanceof String)) {
                throw new RuntimeException("Unexpected object type: " + expiryObj.getClass());
            }

            expiry = Instant.parse((String)expiryObj);
        }

        return expiry != null && expiry.compareTo(Instant.now()) <= 0;
    }

    public Map<String, Object> refresh(Map<String, Object> config) {
        throw new IllegalStateException("Unimplemented");
    }

    static {
        KubeConfig.registerAuthenticator(new GCPAuthenticator());
        log = LoggerFactory.getLogger(GCPAuthenticator.class);
    }
}
