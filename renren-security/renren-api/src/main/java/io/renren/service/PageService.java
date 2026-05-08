package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.entity.PageEntity;

import java.util.List;

public interface PageService extends BaseService<PageEntity> {
    
    List<PageEntity> getListBySiteId(Long siteId);
    
    PageEntity getByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
    
    PageEntity create(PageEntity page, Long userId);
    
    PageEntity update(PageEntity page, Long userId);
}
