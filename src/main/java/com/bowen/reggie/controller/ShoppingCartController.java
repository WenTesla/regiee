package com.bowen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bowen.reggie.common.BaseContext;
import com.bowen.reggie.common.R;
import com.bowen.reggie.entity.ShoppingCart;
import com.bowen.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart);
        //设置用户id，指定当前是那个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询是否为菜品或者套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null) {
            //添加到购物车的是菜品
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加购物车的是套餐
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        // select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        //如果已经存在，+1
        if (cartServiceOne != null) {
            //更新
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);

        }
        //如果不存在，=1
        else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R clean(){
        log.info("清空当前用户的购物车!");
        // delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车成功！");

    }

    @PostMapping("sub")
    public R delete(@RequestBody ShoppingCart shoppingCart){
        log.info("dishId:{},setmealId:{}",shoppingCart.getDishId(),shoppingCart.getSetmealId());
        if (shoppingCart == null) {
            return R.error("请求数据不存在");
        }
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        //如果请求的是菜单
        if (shoppingCart.getDishId() != null) {
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            ShoppingCart cart = shoppingCartService.getOne(wrapper);
            //判断是否大于一
            if (cart.getNumber() > 1){
                //大于一
                Integer number = cart.getNumber();
                cart.setNumber(number-1);
                shoppingCartService.updateById(cart);
                return R.success("菜品减一");
            }
            else {
                //等于1，删除
                shoppingCartService.removeById(cart.getId());
                return R.success("删除菜品成功!");
            }

        }
        //如果请求的是套餐
        if (shoppingCart.getSetmealId() != null) {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

            shoppingCartService.removeById(shoppingCart.getId());
            return R.success("删除套餐成功");
        }

        return null;
    }
}
