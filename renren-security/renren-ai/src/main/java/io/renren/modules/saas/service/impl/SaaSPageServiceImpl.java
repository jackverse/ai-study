package io.renren.modules.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.saas.config.TenantContext;
import io.renren.modules.saas.dao.SiteDao;
import io.renren.modules.saas.dao.SitePageDao;
import io.renren.modules.saas.entity.SiteEntity;
import io.renren.modules.saas.entity.SitePageEntity;
import io.renren.modules.saas.service.SaaSPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SaaS页面管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaaSPageServiceImpl extends ServiceImpl<SitePageDao, SitePageEntity> implements SaaSPageService {

    private final SiteDao siteDao;

    @Override
    @Transactional
    public SitePageEntity createPage(Long siteId, String name, String title, String path, String pageType) {
        Long tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();
        
        SiteEntity site = siteDao.selectById(siteId);
        if (site == null) {
            throw new RuntimeException("站点不存在或无权限");
        }
        
        SitePageEntity page = new SitePageEntity();
        page.setSiteId(siteId);
        page.setName(name);
        page.setTitle(title);
        page.setPath(path);
        page.setPageType(pageType != null ? pageType : "static");
        page.setStatus(0);
        page.setAiGenerated(0);
        page.setSort(0);
        page.setCreator(userId);
        page.setCreateTime(LocalDateTime.now());
        
        this.save(page);
        log.info("页面创建成功 - id: {}, name: {}, siteId: {}", page.getId(), name, siteId);
        return page;
    }

    @Override
    @Transactional
    public SitePageEntity updatePageContent(Long pageId, String layout, String content, 
                                             String seoTitle, String seoDescription) {
        Long tenantId = TenantContext.getTenantId();
        
        SitePageEntity page = this.getById(pageId);
        if (page == null) {
            throw new RuntimeException("页面不存在");
        }
        
        SiteEntity site = siteDao.selectById(page.getSiteId());
        if (site == null) {
            throw new RuntimeException("站点不存在");
        }
        
        if (layout != null) page.setLayout(layout);
        if (content != null) page.setContent(content);
        if (seoTitle != null) page.setSeoTitle(seoTitle);
        if (seoDescription != null) page.setSeoDescription(seoDescription);
        page.setUpdateTime(LocalDateTime.now());
        
        this.updateById(page);
        log.info("页面更新成功 - pageId: {}", pageId);
        return page;
    }

    @Override
    @Transactional
    public void deletePage(Long pageId) {
        Long tenantId = TenantContext.getTenantId();
        
        SitePageEntity page = this.getById(pageId);
        if (page == null) {
            throw new RuntimeException("页面不存在");
        }
        
        SiteEntity site = siteDao.selectById(page.getSiteId());
        if (site == null) {
            throw new RuntimeException("站点不存在");
        }
        
        this.removeById(pageId);
        log.info("页面删除成功 - pageId: {}", pageId);
    }

    @Override
    public List<SitePageEntity> getPagesBySite(Long siteId) {
        LambdaQueryWrapper<SitePageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SitePageEntity::getSiteId, siteId);
        wrapper.orderByAsc(SitePageEntity::getSort);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public SitePageEntity generatePageByAI(Long siteId, String name, String title, String path, 
                                            String aiContent, String layout) {
        SitePageEntity page = createPage(siteId, name, title, path, "ai-generated");
        page.setAiGenerated(1);
        page.setContent(aiContent);
        page.setLayout(layout);
        page.setStatus(1);
        page.setPublishedAt(LocalDateTime.now());
        this.updateById(page);
        
        log.info("AI页面生成成功 - pageId: {}, name: {}", page.getId(), name);
        return page;
    }
}
