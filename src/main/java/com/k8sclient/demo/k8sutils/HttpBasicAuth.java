package com.k8sclient.demo.k8sutils;

import okhttp3.Credentials;

import java.util.List;
import java.util.Map;

/**
 * @author liyang(leonasli)
 * @className HttpBasicAuth
 * @description TODO
 * @create 2022/4/27 15:13
 **/
public class HttpBasicAuth implements Authentication {
    private String username;
    private String password;

    public HttpBasicAuth() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams, Map<String, String> cookieParams) {
        if (this.username != null || this.password != null) {
            headerParams.put("Authorization", Credentials.basic(this.username == null ? "" : this.username, this.password == null ? "" : this.password));
        }
    }
}