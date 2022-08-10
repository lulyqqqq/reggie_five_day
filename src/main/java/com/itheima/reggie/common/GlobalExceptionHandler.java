package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *
 * @ClassName: GlobalExceptionHandler
 * @author: mafangnian
 * @date: 2022/8/8 16:53
 * @Blog: null
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@RestControllerAdvice
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 数据库唯一字段重复异常处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常出来方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(@NotNull CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
