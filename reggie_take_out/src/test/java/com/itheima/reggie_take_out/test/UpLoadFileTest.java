package com.itheima.reggie_take_out.test;

import org.junit.jupiter.api.Test;

public class UpLoadFileTest {

    @Test
    public void test1() {
        String fileName = "abc.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
