package io.renren.modules.ai.dao;

import io.renren.modules.ai.entity.PageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 页面 Mapper
 */
@Mapper
public interface PageDao extends BaseMapper<PageEntity> {

    /**
     * 获取站点的页面列表
     */
    @Select("SELECT * FROM page WHERE tenant_id = #{tenantId} AND site_id = #{siteId} ORDER BY is_home DESC, created_at ASC")
    List<PageEntity> selectBySiteId(@Param("tenantId") String tenantId, @Param("siteId") Long siteId);

    /**
     * 获取站点的首页
     */
    @Select("SELECT * FROM page WHERE tenant_id = #{tenantId} AND site_id = #{siteId} AND is_home = 1 LIMIT 1")
    PageEntity selectHomePage(@Param("tenantId") String tenantId, @Param("siteId") Long siteId);
}
