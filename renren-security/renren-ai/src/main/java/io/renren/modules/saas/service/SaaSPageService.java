package io.renren.modules.saas.service;

import io.renren.modules.saas.entity.SitePageEntity;
import java.util.List;

/**
 * SaaS页面管理服务接口
 */
public interface SaaSPageService {

    /**
     * 创建页面
     */
    SitePageEntity createPage(Long siteId, String name, String title, String path, String pageType);

    /**
     * 更新页面内容
     */
    SitePageEntity updatePageContent(Long pageId, String layout, String content, String seoTitle, String seoDescription);

    /**
     * 删除页面
     */
    void deletePage(Long pageId);

    /**
     * 获取站点的所有页面
     */
    List<SitePageEntity> getPagesBySite(Long siteId);

    /**
     * AI生成页面内容
     */
    SitePageEntity generatePageByAI(Long siteId, String name, String title, String path, String aiContent, String layout);
}
