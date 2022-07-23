package com.itheima.reggie_take_out.test;

import org.openjdk.jol.info.ClassLayout;

public class ObjTest {

    static class T {
    }
    public static void main(String[] args) {
        T t = new T();
        System.out.println(ClassLayout.parseInstance(t).toPrintable());

        synchronized (t) {
            System.out.println(ClassLayout.parseInstance(t).toPrintable());
        }

        System.out.println(ClassLayout.parseInstance(t).toPrintable());
    }
}
