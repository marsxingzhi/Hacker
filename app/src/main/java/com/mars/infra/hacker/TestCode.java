package com.mars.infra.hacker;

import android.util.Log;

/**
 * Created by Mars on 2022/4/9
 */
class TestCode {

    // 测试通过
    private static void test1() {
//        Log.e("TestCode", "执行 test1 方法");

        System.out.println("哈哈 - 1");
        Logger logger = new Logger();
        logger.ee("TestCode", "执行 test1 方法");
//        logger.ee2("TestCode", 123456789);
        login();
        logger.ee("TestCode", 1024);
        logger.ee("TestCode", 123.0f);
//        System.out.println("哈哈 - 2");

    }

    private static void login() {
//        System.out.println("哈哈 - 2");
        Log.e("gy", "login");
    }

//    public void test2(int flag) {
//        Log.e("TestCode", "执行 test2 方法");
//    }
//
//    public void test3(String msg) {
//        Log.e("TestCode", "执行 test3 方法, msg = " + msg);
//    }
}
