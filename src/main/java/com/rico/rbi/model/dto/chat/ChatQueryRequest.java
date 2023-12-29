package com.rico.rbi.model.dto.chat;

import com.rico.rbi.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatQueryRequest extends PageRequest implements Serializable {

    private Long id;
    private int modelId;
    private Long userId;

    private static final long serialVersionUID = 1L;
}
