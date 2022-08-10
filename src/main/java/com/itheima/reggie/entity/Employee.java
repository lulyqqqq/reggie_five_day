package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    //插入时,进行自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //插入或更新时,进行自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //插入时,进行自动填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //插入或更新时,进行自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
