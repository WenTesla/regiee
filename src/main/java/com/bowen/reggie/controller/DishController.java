package com.bowen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.reggie.common.R;
import com.bowen.reggie.dto.DishDto;
import com.bowen.reggie.entity.Category;
import com.bowen.reggie.entity.Dish;
import com.bowen.reggie.service.CategoryService;
import com.bowen.reggie.service.DishFlavorService;
import com.bowen.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1.批量删除 ️️
 * 2.批量起售  ️✔️
 * 3.批量停售  ️✔️
 * 4.新建菜品 ✔️️
 * 5.修改 ️✔️
 * 6.起售（停售) ✔️
 * 7.删除 ️️
 * 8.查询 ✔️
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品(json数据)
     *
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");

    }

    /**
     * 菜品分类分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        //原始数据
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //封装后的数据
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页
        dishService.page(pageInfo, queryWrapper);
        /*
        //官方使用流的方法
        // 这个地方将pageInfo中的属性除了recodes外全部拷贝到dishDtoPage中
        // 因为除了recodes中的属性（就是Page（Dish）和Page（DishDto））不同其他的page等都是一样的
        //对象拷贝（忽略record)
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //拷贝Records
        List<Dish> records = pageInfo.getRecords();
        //使用流
        List<DishDto> list = records.stream().map((item)->{
            //创建dishDto对象
            DishDto dishDto = new DishDto();
            //对象拷贝（将item拷贝到dishDto）
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            //如果查询到，执行（防止报错）
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
         */
        //拷贝对象（忽略records）
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取pageInfo的records数据
        List<Dish> records = pageInfo.getRecords();
        //创建新链表用于存储dishDtoList
        ArrayList<DishDto> list = new ArrayList<>();
        //获取迭代器
        Iterator<Dish> dishIterator = records.iterator();
        while (dishIterator.hasNext()) {
            //获取对象
            Dish dish = dishIterator.next();
            DishDto dishDto = new DishDto();
            //将dish拷贝到dishDto
            BeanUtils.copyProperties(dish,dishDto);
            //获取dish的categoryId
            Long categoryId = dish.getCategoryId();
            //查表获取category的实体对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //给dishDto对象set CategoryName值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
                list.add(dishDto);
            }

        }
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功！");

    }

    /**
     * 根据条件查询菜品数据
     * 使用实体类接收（更加通用）
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1的
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 批量起售(停售)
     * @param isSelling
     * @param ids
     * @return
     */
    @PostMapping("/status/{isSelling}")
    public R<String> changeStatus(@PathVariable int isSelling,@RequestParam List<Long> ids){
        log.info("iselling:{},ids:{}",isSelling,ids.toArray());
        dishService.updateStatusByIds(isSelling,ids);

        return R.success("菜品状态修改成功");
    }

    /**
     * 单批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids){
        log.info("ids为:{}",ids.toArray());
        return null;
    }

}
