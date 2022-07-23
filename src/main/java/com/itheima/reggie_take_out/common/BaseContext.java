package com.itheima.reggie_take_out.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 * threadLocal 是线程的局部变量，以线程为作用域，每个线程单独保存自己的一份副本
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     *
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     *
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
