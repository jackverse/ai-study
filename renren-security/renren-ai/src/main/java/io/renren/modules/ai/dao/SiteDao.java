package io.renren.modules.ai.dao;

import io.renren.modules.ai.entity.SiteEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 站点 Mapper
 */
@Mapper
public interface SiteDao extends BaseMapper<SiteEntity> {

    /**
     * 根据租户ID获取站点列表
     */
    @Select("SELECT * FROM site WHERE tenant_id = #{tenantId} AND status = 1 ORDER BY created_at DESC")
    List<SiteEntity> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 根据租户ID获取第一个站点
     */
    @Select("SELECT * FROM site WHERE tenant_id = #{tenantId} AND status = 1 ORDER BY created_at ASC LIMIT 1")
    SiteEntity selectFirstByTenantId(@Param("tenantId") String tenantId);
}
