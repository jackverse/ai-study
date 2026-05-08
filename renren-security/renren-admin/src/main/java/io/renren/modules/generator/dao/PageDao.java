package io.renren.modules.generator.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.generator.entity.PageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PageDao extends BaseDao<PageEntity> {
    
    List<PageEntity> selectBySiteId(@Param("siteId") Long siteId);
    
    PageEntity selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
