package com.mars.infra.hacker.test;

import android.util.Log;

/**
 * Created by Mars on 2022/4/25
 */
public class ThreadTest {

    public static void test(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    // current thread = Thread-6
                    Log.e("gy", "current thread = " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread  t = new Thread(runnable);
        t.start();
        Log.e("gy", "thread = " + t.getClass());
    }

    public static void test1() {
        Thread  t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                // current thread = Thread-6
                Log.e("gy", "current thread = " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        Log.e("gy", "thread = " + t.getClass());
    }

    public static void test2() {
        Thread  t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                // current thread = Thread-6
                Log.e("gy", "current thread = " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "ThreadTest---test3");
        t.start();
        Log.e("gy", "thread = " + t.getClass());
    }

}
