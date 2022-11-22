package com.bowen;

import com.bowen.reggie.ReggieApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 测试redis
 */
@SpringBootTest(classes = {ReggieApplication.class})
public class redis {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作String
     */
    @Test
    public void testSetString(){
        System.out.println("redis已启动");
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        System.out.println(valueOperations);
        valueOperations.set("city","tianjin");
        valueOperations.set("city123","value123",10l, TimeUnit.MILLISECONDS);

    }
    @Test
    public void testGetString(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object city = valueOperations.get("city");
        System.out.println(city);
    }

    /**
     * 操作hash
     */

    @Test
    public void testSetHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("001","name","BoWen");
        hashOperations.put("001","age","18");

    }
    @Test
    public void testGetHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object age = hashOperations.get("001", "age");
        System.out.println(age);
        Set keys = hashOperations.keys("001");
        for (Object key:keys)
            System.out.println(key);
        List values = hashOperations.values("001");
        for (Object value : values) {
            System.out.println(value);
        }

    }
    /**
     * 操作List集合的数据
     */
    @Test
    public void testList(){
        //存值
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("mylist","a");
        listOperations.leftPushAll("mylist","b","c");
        //取值
        List mylist = listOperations.range("mylist", 0, -1);
        for (Object o : mylist) {
            System.out.println(o);
        }
        //获取列表长度
        Long size = listOperations.size("mylist");
        int intValue = size.intValue();
        for (int i = 0; i < intValue; i++) {
            //出队列
            //删除
            Object element = listOperations.rightPop("mylist");

        }



    }



}
