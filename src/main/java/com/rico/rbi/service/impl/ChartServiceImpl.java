package com.rico.rbi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rico.rbi.common.ErrorCode;
import com.rico.rbi.exception.BusinessException;
import com.rico.rbi.exception.ThrowUtils;
import com.rico.rbi.model.dto.chart.ChartAddRequest;
import com.rico.rbi.model.entity.Chart;
import com.rico.rbi.model.entity.User;
import com.rico.rbi.model.vo.BiResponse;
import com.rico.rbi.mq.BiMessageProducer;
import com.rico.rbi.service.ChartService;
import com.rico.rbi.mapper.ChartMapper;
import com.rico.rbi.service.UserService;
import com.rico.rbi.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    @Resource
    private ChartMapper chartMapper;

    @Resource
    private UserService userService;

    @Resource
    private BiMessageProducer biMessageProducer;
    @Override
    public long addChart(ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        int result = chartMapper.insert(chart);
        ThrowUtils.throwIf(result == 0, ErrorCode.OPERATION_ERROR);
        return chart.getId();
    }
    @Override
    public boolean deleteChart(long id, User user) {
        // 判断是否存在
        Chart oldChart = chartMapper.selectById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        int b = chartMapper.deleteById(id);
        ThrowUtils.throwIf(b==0,ErrorCode.OPERATION_ERROR);
        return true;
    }
    @Override
    public BiResponse genChartByAiAsyncMq(MultipartFile multipartFile, String goal, String chartType, String name, User loginUser) {
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");

        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        int saveResult = chartMapper.insert(chart);
        ThrowUtils.throwIf(saveResult == 0, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        long newChartId = chart.getId();
        biMessageProducer.sendMessage(String.valueOf(newChartId));
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return biResponse;
    }
}




