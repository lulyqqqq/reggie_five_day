package com.itheima.reggie.common;

/**
 * 自定义业务异常类
 * @ClassName: CustomException
 * @author: mafangnian
 * @date: 2022/8/9 17:07
 * @Blog: null
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
