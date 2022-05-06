package com.k8sclient.demo.k8sutils;


import java.util.List;
import java.util.Map;

public interface Authentication {
    void applyToParams(List<Pair> var1, Map<String, String> var2, Map<String, String> var3);
}
