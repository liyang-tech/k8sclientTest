package com.k8sclient.demo.k8sutils;

import java.io.IOException;
import java.util.ArrayList;

public interface ConfigPersister {
    void save(ArrayList<Object> var1, ArrayList<Object> var2, ArrayList<Object> var3, Object var4, String var5) throws IOException;
}
