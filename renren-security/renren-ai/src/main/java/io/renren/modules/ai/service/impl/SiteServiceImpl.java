package io.renren.modules.ai.service.impl;

import io.renren.modules.ai.dao.SiteDao;
import io.renren.modules.ai.entity.SiteEntity;
import io.renren.modules.ai.service.SiteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点服务实现
 */
@Service
public class SiteServiceImpl implements SiteService {

    private final SiteDao siteDao;

    public SiteServiceImpl(SiteDao siteDao) {
        this.siteDao = siteDao;
    }

    @Override
    public List<SiteEntity> getByTenantId(String tenantId) {
        return siteDao.selectByTenantId(tenantId);
    }

    @Override
    public SiteEntity getFirstByTenantId(String tenantId) {
        return siteDao.selectFirstByTenantId(tenantId);
    }
}
