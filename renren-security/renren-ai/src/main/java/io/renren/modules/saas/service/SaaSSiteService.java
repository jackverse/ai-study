package io.renren.modules.saas.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.modules.saas.entity.SiteEntity;

/**
 * SaaS站点管理服务接口
 */
public interface SaaSSiteService {

    /**
     * 创建站点
     */
    SiteEntity createSite(String name, String description, String category);

    /**
     * 更新站点
     */
    SiteEntity updateSite(Long siteId, String name, String config);

    /**
     * 发布站点
     */
    SiteEntity publishSite(Long siteId);

    /**
     * 获取租户的站点列表
     */
    IPage<SiteEntity> getSites(Integer pageNum, Integer pageSize);

    /**
     * 获取站点详情
     */
    SiteEntity getSiteInfo(Long siteId);

    /**
     * 应用模板到站点
     */
    void applyTemplate(Long siteId, Long templateId);
}
