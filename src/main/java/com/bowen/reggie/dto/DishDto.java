package com.bowen.reggie.dto;

import com.bowen.reggie.entity.Dish;
import com.bowen.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过继承封装
 */
@Data
public class DishDto extends Dish {

    /**
     * 菜品口味（链表）
     */

    private List<DishFlavor> flavors = new ArrayList<>();

    /**
     * 套餐名称
     */

    private String categoryName;


    private Integer copies;


}
