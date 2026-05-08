package io.renren.modules.generator.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.generator.entity.SiteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteDao extends BaseDao<SiteEntity> {
    
    List<SiteEntity> selectByUserId(@Param("userId") Long userId);
    
    SiteEntity selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
