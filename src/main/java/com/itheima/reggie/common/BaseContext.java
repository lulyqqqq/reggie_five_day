package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类
 * 用户保存和获取当前登录用户的id
 * @ClassName: BaseContext
 * @author: mafangnian
 * @date: 2022/8/9 14:28
 * @Blog: null
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 存放当前线程的id属性值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 直接将id属性值返回出去
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
