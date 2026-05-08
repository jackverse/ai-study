package io.renren.modules.ai.service;

import io.renren.modules.ai.entity.TenantEntity;

/**
 * 租户服务接口
 */
public interface TenantService {

    /**
     * 根据租户ID获取租户信息
     */
    TenantEntity getByTenantId(String tenantId);

    /**
     * 更新AI使用次数
     */
    void incrementAiUsed(String tenantId);
}
