package com.bowen.reggie.dto;

import com.bowen.reggie.entity.OrderDetail;
import com.bowen.reggie.entity.Orders;
import lombok.Data;

import java.util.List;
@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
}
