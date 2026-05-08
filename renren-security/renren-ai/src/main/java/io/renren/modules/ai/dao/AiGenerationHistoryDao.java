package io.renren.modules.ai.dao;

import io.renren.modules.ai.entity.AiGenerationHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI生成历史 Mapper
 */
@Mapper
public interface AiGenerationHistoryDao extends BaseMapper<AiGenerationHistoryEntity> {

    /**
     * 获取租户的AI历史
     */
    @Select("SELECT * FROM ai_generation_history WHERE tenant_id = #{tenantId} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<AiGenerationHistoryEntity> selectByTenantId(@Param("tenantId") String tenantId, @Param("offset") int offset, @Param("limit") int limit);
}
