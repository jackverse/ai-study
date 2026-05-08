package io.renren.modules.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.saas.config.TenantContext;
import io.renren.modules.saas.dao.SiteDao;
import io.renren.modules.saas.dao.SiteTemplateDao;
import io.renren.modules.saas.entity.SiteEntity;
import io.renren.modules.saas.entity.SiteTemplateEntity;
import io.renren.modules.saas.service.SaaSSiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * SaaS站点管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaaSSiteServiceImpl extends ServiceImpl<SiteDao, SiteEntity> implements SaaSSiteService {

    private final SiteTemplateDao templateDao;

    @Override
    @Transactional
    public SiteEntity createSite(String name, String description, String category) {
        Long tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();
        
        SiteEntity site = new SiteEntity();
        site.setTenantId(tenantId != null ? String.valueOf(tenantId) : null);
        site.setSiteName(name);
        site.setDescription(description);
        site.setStatus(0);
        site.setAiEnabled(1);
        site.setVisitCount(0L);
        site.setCreator(userId);
        site.setCreatedAt(new Date());
        
        this.save(site);
        log.info("站点创建成功 - id: {}, name: {}, tenantId: {}", site.getId(), name, tenantId);
        return site;
    }

    @Override
    @Transactional
    public SiteEntity updateSite(Long siteId, String name, String config) {
        Long tenantId = TenantContext.getTenantId();
        
        SiteEntity site = this.getById(siteId);
        if (site == null) {
            throw new RuntimeException("站点不存在或无权限");
        }
        
        if (name != null) site.setSiteName(name);
        if (config != null) site.setConfig(config);
        site.setUpdatedAt(new Date());
        
        this.updateById(site);
        return site;
    }

    @Override
    @Transactional
    public SiteEntity publishSite(Long siteId) {
        Long tenantId = TenantContext.getTenantId();
        
        SiteEntity site = this.getById(siteId);
        if (site == null) {
            throw new RuntimeException("站点不存在或无权限");
        }
        
        site.setStatus(1);
        site.setPublishedAt(new Date());
        site.setUpdatedAt(new Date());
        this.updateById(site);
        
        log.info("站点发布成功 - siteId: {}", siteId);
        return site;
    }

    @Override
    public IPage<SiteEntity> getSites(Integer pageNum, Integer pageSize) {
        Long tenantId = TenantContext.getTenantId();
        Page<SiteEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SiteEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SiteEntity::getTenantId, String.valueOf(tenantId));
        }
        wrapper.orderByDesc(SiteEntity::getCreatedAt);
        return this.page(page, wrapper);
    }

    @Override
    public SiteEntity getSiteInfo(Long siteId) {
        Long tenantId = TenantContext.getTenantId();
        SiteEntity site = this.getById(siteId);
        if (site == null) {
            throw new RuntimeException("站点不存在或无权限");
        }
        if (tenantId != null && !String.valueOf(tenantId).equals(site.getTenantId())) {
            throw new RuntimeException("站点不存在或无权限");
        }
        return site;
    }

    @Override
    @Transactional
    public void applyTemplate(Long siteId, Long templateId) {
        SiteTemplateEntity template = templateDao.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        SiteEntity site = this.getById(siteId);
        if (site == null) {
            throw new RuntimeException("站点不存在");
        }
        
        site.setTemplateId(templateId);
        site.setConfig(template.getConfig());
        site.setUpdatedAt(new Date());
        this.updateById(site);
        
        log.info("模板应用到站点 - siteId: {}, templateId: {}, templateName: {}", 
                 siteId, templateId, template.getName());
    }
}
