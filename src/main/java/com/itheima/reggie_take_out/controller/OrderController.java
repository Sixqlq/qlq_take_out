package com.itheima.reggie_take_out.controller;

import com.itheima.reggie_take_out.common.Result;
import com.itheima.reggie_take_out.entity.Order;
import com.itheima.reggie_take_out.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Order order) {
        log.info("订单数据：{}", order);
        orderService.submit(order);
        return Result.success("下单成功");
    }
}
