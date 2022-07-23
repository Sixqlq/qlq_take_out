package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.entity.Order;

public interface OrderService extends IService<Order> {

    /**
     * 用户下单
     * @param order
     */
    public void submit(Order order);
}
