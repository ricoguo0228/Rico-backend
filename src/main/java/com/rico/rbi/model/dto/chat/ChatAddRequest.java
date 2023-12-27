package com.rico.rbi.model.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 
 */
@Data
public class ChatAddRequest implements Serializable {

    /**
     * 询问内容
     */
    private String askContent;
    /**
     *
     */
    private String modelID;

    private static final long serialVersionUID = 1L;
}
