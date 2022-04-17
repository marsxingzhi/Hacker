package com.mars.infra.hacker;

/**
 * Created by Mars on 2022/4/16
 */
public class Logger {

    public int ee(String tag, String msg) {
        System.out.println("tag = " + tag + ", msg = " + msg);
        return -1;
    }

    public int ee(String tag, int code) {
        System.out.println("tag = " + tag + ", code = " + code);
        return -1;
    }

    public int ee(String tag, float code) {
        System.out.println("tag = " + tag + ", code = " + code);
        return -1;
    }
}
