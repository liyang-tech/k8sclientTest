package com.k8sclient.demo.k8sutils;

import java.util.List;
import java.util.Map;

/**
 * @author liyang(leonasli)
 * @className ApiResponse
 * @description TODO
 * @create 2022/4/27 15:23
 **/
public class ApiResponse<T> {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final T data;

    public ApiResponse(int statusCode, Map<String, List<String>> headers) {
        this(statusCode, headers, (T) null);
    }

    public ApiResponse(int statusCode, Map<String, List<String>> headers, T data) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.data = data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public T getData() {
        return this.data;
    }
}