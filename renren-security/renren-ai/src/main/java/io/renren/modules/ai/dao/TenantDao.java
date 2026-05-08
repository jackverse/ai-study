package io.renren.modules.ai.dao;

import io.renren.modules.ai.entity.TenantEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper
 */
@Mapper
public interface TenantDao extends BaseMapper<TenantEntity> {
}
