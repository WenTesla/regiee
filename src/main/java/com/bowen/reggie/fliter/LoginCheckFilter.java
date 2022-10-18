package com.bowen.reggie.fliter;

import com.alibaba.fastjson.JSON;
import com.bowen.reggie.common.BaseContext;
import com.bowen.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
//@ServletComponentScan//配置注解已扫描
//@Component
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j

public class LoginCheckFilter implements Filter {
    //专门用于路径匹配，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取url
        String requestURI = request.getRequestURI();
        //定义不需要请求的请求路径
        String[] urls = {"/employee/login","/employee/logout","/backend/**","/front/**","/common/*"};
        log.info("本次拦截到请求:{}",request.getRequestURI());
        //2.判断请求路径是否需要处理
        boolean check = check(requestURI, urls);
        //放行
        if (check){
            log.info("本次不需要处理:{}",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登录id:{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("当前线程id为{}",id);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //5.未登录则返回未登录状态
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 请求本次请求是否需要放行
     * @param requestUrl
     * @Param urls
     * @return
     */
    public boolean check(String requestUrl,String[] urls){
        for (String url :
                urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match)
                return true;
        }
        return false;
    }
}
