package com.bowen.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.reggie.common.R;
import com.bowen.reggie.entity.Employee;
import com.bowen.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    //注入
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /**
         *
         */
        //1.将页面的提交的密码的用户名查询数据库,使用md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的的用户名查询(mybatis-plus)
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查到则返回结果
        if (emp == null)
            return R.error("登录失败:用户名不存在");
        //4.密码比对，如果不一直则返回登录失败结果
        if (!emp.getPassword().equals(password))
            return R.error("登录失败:密码错误");
        //5.查询是否禁用
        if (emp.getStatus() == 0)
            return R.error("此账号已禁用");
        //6.登录,设置session
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());
        session.setMaxInactiveInterval(30*60);
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工（前端返回Json给后端）
     *
     * @param employee 员工的信息
     * @return 是否成功
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        //设置初始密码(md5加密处理)
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        log.info("员工信息:{}", employee.toString());
        return R.success("新增用户成功");
    }

    /**
     * 员工分页查询
     *
     * @param page     当前页数
     * @param pageSize 每个页面的数量
     * @param name     名称
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构建分页构造器,创建对象
        Page pageInfo = new Page(page, pageSize);
        //构建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getUsername, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工
     *
     * @param employee 通过json获取employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());
//        Long employeeID = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employeeID);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息:{}", id);
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return R.success(employee);

        return R.error("没有查询到对应员工信息");
    }

}
