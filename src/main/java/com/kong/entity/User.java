package com.kong.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 用户信息
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //姓名
    private String name;


    //手机号
    private String phone;


    //性别 0 女 1 男
    private String sex;


    //身份证号
    private String idNumber;


    //头像
    private String avatarUrl; // 用户头像图片的 URL


    //状态 0:禁用，1:正常
    private Integer status;

    private String openid; // 用户唯一标识

    private String nickName; // 用户昵称


    @TableField(select = false,exist = false)
    private String code; // 微信用户code 前端传来的


    private LocalDateTime createTime;
}