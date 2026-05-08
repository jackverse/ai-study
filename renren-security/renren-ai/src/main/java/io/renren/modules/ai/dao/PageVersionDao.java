package io.renren.modules.ai.dao;

import io.renren.modules.ai.entity.PageVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 页面版本 Mapper
 */
@Mapper
public interface PageVersionDao extends BaseMapper<PageVersionEntity> {

    /**
     * 获取页面的版本历史
     */
    @Select("SELECT * FROM page_version WHERE page_id = #{pageId} ORDER BY version DESC")
    List<PageVersionEntity> selectByPageId(@Param("pageId") Long pageId);

    /**
     * 获取最新版本号
     */
    @Select("SELECT MAX(version) FROM page_version WHERE page_id = #{pageId}")
    Integer getMaxVersion(@Param("pageId") Long pageId);
}
