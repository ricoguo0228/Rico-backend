package com.rico.rbi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rico.rbi.annotation.AuthCheck;
import com.rico.rbi.bizmq.BiMessageProducer;
import com.rico.rbi.common.BaseResponse;
import com.rico.rbi.common.DeleteRequest;
import com.rico.rbi.common.ErrorCode;
import com.rico.rbi.common.ResultUtils;
import com.rico.rbi.constant.CommonConstant;
import com.rico.rbi.constant.ModelID;
import com.rico.rbi.constant.UserConstant;
import com.rico.rbi.exception.BusinessException;
import com.rico.rbi.exception.ThrowUtils;
import com.rico.rbi.manager.AiManager;
import com.rico.rbi.manager.RedisLimiterManager;
import com.rico.rbi.model.dto.chart.*;
import com.rico.rbi.model.dto.chat.ChatAddRequest;
import com.rico.rbi.model.dto.chat.ChatQueryRequest;
import com.rico.rbi.model.entity.History;
import com.rico.rbi.model.entity.History;
import com.rico.rbi.model.entity.User;
import com.rico.rbi.model.vo.BiResponse;
import com.rico.rbi.service.ChartService;
import com.rico.rbi.service.HistoryService;
import com.rico.rbi.service.UserService;
import com.rico.rbi.utils.ExcelUtils;
import com.rico.rbi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 聊天接口
 *
 
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Resource
    private HistoryService historyService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private BiMessageProducer biMessageProducer;

    // region 增删改查

    /**
     * 创建
     *
     * @param chatAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChat(@RequestBody ChatAddRequest chatAddRequest, HttpServletRequest request) {
        if (chatAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        History history = new History();
        BeanUtils.copyProperties(chatAddRequest, history);
        User loginUser = userService.getLoginUser(request);
        history.setUserId(loginUser.getId());
        boolean result = historyService.save(history);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newHistoryId = history.getId();
        return ResultUtils.success(newHistoryId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        History oldHistory = historyService.getById(id);
        ThrowUtils.throwIf(oldHistory == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldHistory.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = historyService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        History chart = new History();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        History oldHistory = historyService.getById(id);
        ThrowUtils.throwIf(oldHistory == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = historyService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<History> getChartById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        History history = historyService.getById(id);
        if (history == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(history);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chatQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<History>> listHistoriesByPage(@RequestBody ChatQueryRequest chatQueryRequest,
            HttpServletRequest request) {
        long current = chatQueryRequest.getCurrent();
        long size = chatQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<History> chartPage = historyService.page(new Page<>(current, size),
                getQueryWrapper(chatQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chatQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<History>> listMyChartByPage(@RequestBody ChatQueryRequest chatQueryRequest,
            HttpServletRequest request) {
        if (chatQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chatQueryRequest.setUserId(loginUser.getId());
        long current = chatQueryRequest.getCurrent();
        long size = chatQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<History> chartPage = historyService.page(new Page<>(current, size),
                getQueryWrapper(chatQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        History chart = new History();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        History oldHistory = historyService.getById(id);
        ThrowUtils.throwIf(oldHistory == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldHistory.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = historyService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 智能分析（同步）
     *
     * @param chatAddRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<History> genChartByAi(@RequestBody ChatAddRequest chatAddRequest, HttpServletRequest request) {
        String askContent = chatAddRequest.getAskContent();
        String id = chatAddRequest.getId();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(askContent), ErrorCode.PARAMS_ERROR, "询问内容为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(askContent) && askContent.length() > 100, ErrorCode.PARAMS_ERROR, "问题过长");

        User loginUser = userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());

        long biModelId = new ModelID().getModelID(id);

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append(askContent);

        History history = new History();
        history.setAskContent(askContent);
        history.setUserId(loginUser.getId());

        boolean saveResult = historyService.save(history);
        if(!saveResult){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "啊哦，数据库开小差了");
        }
        String result = aiManager.doChat(biModelId, userInput.toString());

        // 插入到数据库
        long updateHistoryId = history.getId();
        History updateHistory = historyService.getById(updateHistoryId);
        if(updateHistory == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据已经被清理了");
        }
        updateHistory.setReplyContent(result);
        boolean updateResult = historyService.updateById(updateHistory);
        ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "图表更新失败");
        return ResultUtils.success(updateHistory);
    }

    /**
     * 智能分析（异步消息队列）
     *
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<History> genChatByAiAsyncMq(@RequestBody ChatAddRequest chatAddRequest, HttpServletRequest request) {
        String askContent = chatAddRequest.getAskContent();
        String id = chatAddRequest.getId();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(askContent), ErrorCode.PARAMS_ERROR, "询问内容为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(askContent) && askContent.length() > 100, ErrorCode.PARAMS_ERROR, "问题过长");

        User loginUser = userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());

        long biModelId = new ModelID().getModelID(id);

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append(askContent);

        History history = new History();
        history.setAskContent(askContent);
        history.setUserId(loginUser.getId());

        boolean saveResult = historyService.save(history);
        if(!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "啊哦，数据库开小差了");
        }
        //todo:修改为异步操作，优先修改rabbitMQ添加新的消息队列
        String result = aiManager.doChat(biModelId, userInput.toString());

        // 插入到数据库
        long updateHistoryId = history.getId();
        History updateHistory = historyService.getById(updateHistoryId);
        if(updateHistory == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据已经被清理了");
        }
        updateHistory.setReplyContent(result);
        boolean updateResult = historyService.updateById(updateHistory);
        ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "图表更新失败");
        return ResultUtils.success(updateHistory);
    }

    /**
     * 获取查询包装类
     *
     * @param chatQueryRequest
     * @return
     */
    private QueryWrapper<History> getQueryWrapper(ChatQueryRequest chatQueryRequest) {
        QueryWrapper<History> queryWrapper = new QueryWrapper<>();
        if (chatQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatQueryRequest.getId();
        Long userId = chatQueryRequest.getUserId();
        int modelId = chatQueryRequest.getModelId();
        String sortField = chatQueryRequest.getSortField();
        String sortOrder = chatQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(modelId), "modelId", modelId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


}
