package com.rico.rbi.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *

 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 密码
     */
    private String userPassword;
    /**
     * 密码
     */
    private String sureUserPassword;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}