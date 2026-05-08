package io.renren.modules.ai.service;

import io.renren.modules.ai.entity.PageEntity;
import io.renren.modules.ai.entity.PageVersionEntity;

import java.util.List;
import java.util.Map;

/**
 * 页面服务接口
 */
public interface PageService {

    /**
     * 获取页面详情
     */
    PageEntity getById(Long pageId);

    /**
     * 获取站点的页面列表
     */
    List<PageEntity> getSitePages(String tenantId, Long siteId);

    /**
     * 创建页面
     */
    PageEntity create(PageEntity page);

    /**
     * 更新页面
     */
    void update(PageEntity page);

    /**
     * 删除页面
     */
    void delete(Long pageId);

    /**
     * 发布页面
     */
    void publish(Long pageId);

    /**
     * 保存页面版本
     */
    void saveVersion(Long pageId, String config, String changeLog, String createdBy);

    /**
     * 获取页面版本历史
     */
    List<PageVersionEntity> getVersionHistory(Long pageId);

    /**
     * 回退到指定版本
     */
    void rollback(Long pageId, Integer version);

    /**
     * 解析页面配置JSON
     */
    Map<String, Object> parseConfig(String config);
}
