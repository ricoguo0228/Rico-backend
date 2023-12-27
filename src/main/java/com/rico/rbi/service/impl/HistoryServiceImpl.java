package com.rico.rbi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rico.rbi.model.entity.History;
import com.rico.rbi.service.HistoryService;
import com.rico.rbi.mapper.HistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author guorui
* @description 针对表【History】的数据库操作Service实现
* @createDate 2023-12-25 21:30:40
*/
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History>
    implements HistoryService{

}




