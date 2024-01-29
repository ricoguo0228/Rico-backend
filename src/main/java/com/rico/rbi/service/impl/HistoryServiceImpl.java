package com.rico.rbi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rico.rbi.common.ErrorCode;
import com.rico.rbi.exception.BusinessException;
import com.rico.rbi.exception.ThrowUtils;
import com.rico.rbi.model.dto.chat.ChatAddRequest;
import com.rico.rbi.model.entity.History;
import com.rico.rbi.model.entity.User;
import com.rico.rbi.mq.BiMessageProducer;
import com.rico.rbi.service.HistoryService;
import com.rico.rbi.mapper.HistoryMapper;
import com.rico.rbi.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author guorui
 * @description 针对表【History】的数据库操作Service实现
 * @createDate 2023-12-25 21:30:40
 */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History>
        implements HistoryService {
    @Resource
    private HistoryMapper historyMapper;

    @Resource
    private UserService userService;

    @Resource
    private BiMessageProducer biMessageProducer;
    @Override
    public long addHistory(ChatAddRequest chatAddRequest, HttpServletRequest request) {
        if (chatAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        History history = new History();
        BeanUtils.copyProperties(chatAddRequest, history);
        User loginUser = userService.getLoginUser(request);
        history.setUserId(loginUser.getId());
        int result = historyMapper.insert(history);
        ThrowUtils.throwIf(result == 0, ErrorCode.OPERATION_ERROR);
        return history.getId();
    }

    @Override
    public boolean deleteHistory(long id, User user) {
        // 判断是否存在
        History history = historyMapper.selectById(id);
        ThrowUtils.throwIf(history == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!history.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        int b = historyMapper.deleteById(id);
        ThrowUtils.throwIf(b == 0, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




