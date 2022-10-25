package com.bowen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.reggie.common.BaseContext;
import com.bowen.reggie.common.R;
import com.bowen.reggie.dto.OrdersDto;
import com.bowen.reggie.entity.OrderDetail;
import com.bowen.reggie.entity.Orders;
import com.bowen.reggie.service.OrderDetailService;
import com.bowen.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("下单的id为{}", orders.getAddressBookId());
        orderService.submit(orders);

        return R.success("下单成功!");
    }

//    order:[{
//        orderTime:'',//下单时间
//                status:undefined,//订单状态 1已结账，2未结账，3已退单，4已完成，5已取消
//                orderDetails:[{
//            name:'',//菜品名称
//                    number:undefined,//数量
//        }],//明细
//        amount:undefined,//实收金额
//                sumNum:0,//菜品总数
//    }],

    /**
     * 用户查看自己下单的参数
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();

        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> orderDtoList = records.stream().map((item) ->{
            OrdersDto orderDto = new OrdersDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);
            BeanUtils.copyProperties(item,orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }
    //抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }



    /**
     * 后台管理页面获取订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("页面为{},页面的尺寸{}", page, pageSize);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     *
     * @param orders
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Orders orders) {
        log.info("订单编号为{},状态码为{}", orders.getNumber(), orders.getStatus());
        orderService.updateById(orders);
        return R.success("订单状态修改成功!");
    }

}