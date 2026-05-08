package io.renren.modules.saas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.saas.entity.AiConversationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话记录Mapper - MyBatis-Plus
 */
@Mapper
public interface AiConversationDao extends BaseMapper<AiConversationEntity> {
}
