package com.rico.rbi.service;

import com.rico.rbi.model.dto.chat.ChatAddRequest;
import com.rico.rbi.model.entity.History;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rico.rbi.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author guorui
* @description 针对表【History】的数据库操作Service
* @createDate 2023-12-25 21:30:40
*/
public interface HistoryService extends IService<History> {

    long addHistory(ChatAddRequest chatAddRequest, HttpServletRequest request);

    boolean deleteHistory(long id, User user);
}
