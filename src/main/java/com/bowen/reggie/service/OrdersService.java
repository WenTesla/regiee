package com.bowen.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bowen.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     */
    public void submit(Orders orders);
}
