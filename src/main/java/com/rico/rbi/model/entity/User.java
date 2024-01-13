package com.rico.rbi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName User
 */
@TableName(value ="User")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String userAccount;

    /**
     * 
     */
    private String userPassword;

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private String userAvatar;

    /**
     * 
     */
    private String userRole;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    /**
     * 
     */
    private String wxOpenID;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
