package com.rico.rbi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 
 */
@Data
public class WxGenChartByAiRequest implements Serializable {

    /**
     * 名称
     */
    private String name;
    /**
     * ID
     */
    private long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}
