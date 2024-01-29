package com.rico.rbi.service;

import com.rico.rbi.common.BaseResponse;
import com.rico.rbi.model.dto.chart.ChartAddRequest;
import com.rico.rbi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rico.rbi.model.entity.User;
import com.rico.rbi.model.vo.BiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface ChartService extends IService<Chart> {
    long addChart(ChartAddRequest chartAddRequest, HttpServletRequest request);

    boolean deleteChart(long id, User user);

    BiResponse genChartByAiAsyncMq(MultipartFile multipartFile, String goal, String chartType, String name, User loginUser);
}
