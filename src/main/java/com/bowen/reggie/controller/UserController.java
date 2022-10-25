package com.bowen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bowen.reggie.common.R;
import com.bowen.reggie.entity.User;
import com.bowen.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.alter.ValidateConstraint;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 移动端用户发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info("用户手机号:{}",user.getPhone());

        //获取手机号
        String phoneNumber = user.getPhone();

        if (StringUtils.isNotEmpty(phoneNumber)){
            //生成随机的4位验证码
//            String code = ValidateCodeUtils.generateValidateCode(6).toString();
//            log.info("code={}", code);
//
//            //调用阿里云提供的短信服务API完成发送短信
////            SMSUtils.sendMessage("瑞吉外卖","",phone,code);
//
//            //需要将生成的验证码保存到Session
//            session.setAttribute(phone, code);

            return R.success("验证码发送成功");
        }

        //需要将生成的验证码保存起来

        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user , HttpSession  session){
        String phone = user.getPhone();
        log.info("用户手机号:{}",phone);
        //判断用户是否注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User customer= userService.getOne(wrapper);
        //如果用户未注册
        if (customer == null) {
            //用户注册,创建一个新对象
            customer=new User();
            customer.setStatus(1);
            customer.setPhone(phone);
            userService.save(customer);
        }

        session.setAttribute("user",customer.getId());

        return R.success(customer);

    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }
}
