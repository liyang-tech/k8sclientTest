package com.k8sclient.demo.authenticators;

import java.util.Map;

public interface Authenticator {
    String getName();

    String getToken(Map<String, Object> var1);

    boolean isExpired(Map<String, Object> var1);

    Map<String, Object> refresh(Map<String, Object> var1);
}
