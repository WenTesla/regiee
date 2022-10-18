package com.bowen.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
//注解：拦截(注解类)
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@RestController
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 进行异常处理(捕获异常类)
     * @return 账号存在/未知错误
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            String message="账号:"+split[2]+"已存在";
            return R.error(message);
        }
        else {
            return R.error("未知错误");
        }

    }

    /**
     * 进行异常处理(捕获异常类)
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.error(exception.getMessage());

        return R.error(exception.getMessage());

    }


}
