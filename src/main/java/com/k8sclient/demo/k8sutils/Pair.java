package com.k8sclient.demo.k8sutils;

/**
 * @author liyang(leonasli)
 * @className Pair
 * @description TODO
 * @create 2022/4/27 15:07
 **/
public class Pair {
    private String name = "";
    private String value = "";

    public Pair(String name, String value) {
        this.setName(name);
        this.setValue(value);
    }

    private void setName(String name) {
        if (this.isValidString(name)) {
            this.name = name;
        }
    }

    private void setValue(String value) {
        if (this.isValidString(value)) {
            this.value = value;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    private boolean isValidString(String arg) {
        if (arg == null) {
            return false;
        } else {
            return !arg.trim().isEmpty();
        }
    }
}