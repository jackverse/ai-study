package io.renren.dao;

import io.renren.common.dao.BaseDao;
import io.renren.entity.PageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PageDao extends BaseDao<PageEntity> {
    
    List<PageEntity> selectBySiteId(@Param("siteId") Long siteId);
}
