package com.example.instabug.utils;

import java.io.Serializable;

public class StringPair implements Serializable {
    private String first, second;

    StringPair() {
    }

    public StringPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
