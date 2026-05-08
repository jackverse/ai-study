package io.renren.modules.ai.service;

import io.renren.modules.ai.entity.SiteEntity;

import java.util.List;

/**
 * 站点服务接口
 */
public interface SiteService {

    /**
     * 获取租户的站点列表
     */
    List<SiteEntity> getByTenantId(String tenantId);

    /**
     * 获取租户的第一个站点
     */
    SiteEntity getFirstByTenantId(String tenantId);
}
