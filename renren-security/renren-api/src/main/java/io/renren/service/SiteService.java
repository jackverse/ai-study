package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.entity.SiteEntity;

import java.util.List;

public interface SiteService extends BaseService<SiteEntity> {
    
    List<SiteEntity> getListByUserId(Long userId);
    
    SiteEntity getByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
    
    SiteEntity create(SiteEntity site, Long userId);
    
    SiteEntity update(SiteEntity site, Long userId);
}
