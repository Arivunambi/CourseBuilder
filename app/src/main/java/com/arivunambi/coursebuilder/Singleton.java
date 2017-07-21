package com.arivunambi.coursebuilder;

/**
 * Created by arivu on 10/22/2016.
 */
public class Singleton {
    private Singleton() { }
    private static Singleton instance = new Singleton();

    public static Singleton getInstance() {
        return instance;
    }
}