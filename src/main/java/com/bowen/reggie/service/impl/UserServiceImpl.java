package com.bowen.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bowen.reggie.entity.User;
import com.bowen.reggie.mapper.UserMapper;
import com.bowen.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


}
