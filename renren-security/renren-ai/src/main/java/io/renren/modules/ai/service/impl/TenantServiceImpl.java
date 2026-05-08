package io.renren.modules.ai.service.impl;

import io.renren.modules.ai.dao.TenantDao;
import io.renren.modules.ai.entity.TenantEntity;
import io.renren.modules.ai.service.TenantService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 租户服务实现
 */
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantDao tenantDao;

    public TenantServiceImpl(TenantDao tenantDao) {
        this.tenantDao = tenantDao;
    }

    @Override
    public TenantEntity getByTenantId(String tenantId) {
        List<TenantEntity> list = tenantDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TenantEntity>()
                .eq("tenant_id", tenantId)
                .eq("status", 1)
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void incrementAiUsed(String tenantId) {
        TenantEntity tenant = getByTenantId(tenantId);
        if (tenant != null) {
            tenant.setAiUsed(tenant.getAiUsed() + 1);
            tenantDao.updateById(tenant);
        }
    }
}
