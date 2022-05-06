package com.k8sclient.demo.utils;

import com.k8sclient.demo.k8sutils.ConfigPersister;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author liyang(leonasli)
 * @className FilePersister
 * @description TODO
 * @create 2022/4/27 14:24
 **/
public class FilePersister implements ConfigPersister {
    File configFile;

    public FilePersister(String filename) {
        this(new File(filename));
    }

    public FilePersister(File file) {
        this.configFile = file;
    }

    public void save(ArrayList<Object> contexts, ArrayList<Object> clusters, ArrayList<Object> users, Object preferences, String currentContext) throws IOException {
        HashMap<String, Object> config = new HashMap();
        config.put("apiVersion", "v1");
        config.put("kind", "Config");
        config.put("current-context", currentContext);
        config.put("preferences", preferences);
        config.put("clusters", clusters);
        config.put("contexts", contexts);
        config.put("users", users);
        synchronized(this.configFile) {
            FileWriter fw = new FileWriter(this.configFile);
            Throwable var9 = null;

            try {
                Yaml yaml = new Yaml();
                yaml.dump(config, fw);
                fw.flush();
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if (fw != null) {
                    if (var9 != null) {
                        try {
                            fw.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        fw.close();
                    }
                }

            }

        }
    }
}
