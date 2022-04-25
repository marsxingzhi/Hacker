package com.mars.infra.thread;

public class HackerThread extends Thread {

    public static String makeThreadName(final String name) {
        return name == null ? "thread-default# " : name;
    }

    public static String makeThreadName(final String name, final String prefix) {
        return name == null ? "thread-default# " + prefix : "thread-default# " + prefix + "#" + name;
    }

    public HackerThread(final Runnable target, final String prefix) {
        super(target, makeThreadName(prefix));
    }

    public HackerThread(final Runnable target, final String name, final String prefix) {
        super(target, makeThreadName(name, prefix));
    }

}
