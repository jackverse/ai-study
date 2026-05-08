package io.renren.modules.saas.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.saas.entity.SiteTemplateEntity;

/**
 * SaaS模板管理服务接口
 */
public interface SaaSTemplateService {

    /**
     * 获取模板列表
     */
    Page<SiteTemplateEntity> getTemplates(String category, Integer pageNum, Integer pageSize);

    /**
     * 获取模板详情
     */
    SiteTemplateEntity getTemplateById(Long templateId);

    /**
     * 应用模板到站点
     */
    void applyTemplateToSite(Long siteId, Long templateId);
}
