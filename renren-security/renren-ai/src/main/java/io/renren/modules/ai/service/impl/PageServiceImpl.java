package io.renren.modules.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.modules.ai.dao.PageDao;
import io.renren.modules.ai.dao.PageVersionDao;
import io.renren.modules.ai.entity.PageEntity;
import io.renren.modules.ai.entity.PageVersionEntity;
import io.renren.modules.ai.service.PageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 页面服务实现
 */
@Service
public class PageServiceImpl implements PageService {

    private final PageDao pageDao;
    private final PageVersionDao pageVersionDao;
    private final ObjectMapper objectMapper;

    public PageServiceImpl(PageDao pageDao, PageVersionDao pageVersionDao, ObjectMapper objectMapper) {
        this.pageDao = pageDao;
        this.pageVersionDao = pageVersionDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageEntity getById(Long pageId) {
        return pageDao.selectById(pageId);
    }

    @Override
    public List<PageEntity> getSitePages(String tenantId, Long siteId) {
        return pageDao.selectBySiteId(tenantId, siteId);
    }

    @Override
    public PageEntity create(PageEntity page) {
        pageDao.insert(page);
        return page;
    }

    @Override
    public void update(PageEntity page) {
        // 保存版本
        saveVersion(page.getId(), page.getConfig(), "更新页面", null);
        // 更新页面
        page.setVersion(page.getVersion() + 1);
        pageDao.updateById(page);
    }

    @Override
    public void delete(Long pageId) {
        pageDao.deleteById(pageId);
    }

    @Override
    public void publish(Long pageId) {
        PageEntity page = getById(pageId);
        if (page != null) {
            page.setStatus(1);
            page.setPublishedAt(new java.util.Date());
            pageDao.updateById(page);
        }
    }

    @Override
    @Transactional
    public void saveVersion(Long pageId, String config, String changeLog, String createdBy) {
        PageVersionEntity version = new PageVersionEntity();
        version.setPageId(pageId);
        version.setConfig(config);
        version.setChangeLog(changeLog);
        version.setCreatedBy(createdBy);
        version.setCreatedAt(new java.util.Date());
        
        // 获取下一个版本号
        Integer maxVersion = pageVersionDao.getMaxVersion(pageId);
        version.setVersion(maxVersion == null ? 1 : maxVersion + 1);
        
        pageVersionDao.insert(version);
    }

    @Override
    public List<PageVersionEntity> getVersionHistory(Long pageId) {
        return pageVersionDao.selectByPageId(pageId);
    }

    @Override
    @Transactional
    public void rollback(Long pageId, Integer version) {
        List<PageVersionEntity> versions = pageVersionDao.selectByPageId(pageId);
        PageVersionEntity targetVersion = versions.stream()
            .filter(v -> v.getVersion().equals(version))
            .findFirst()
            .orElse(null);
        
        if (targetVersion != null) {
            PageEntity page = getById(pageId);
            if (page != null) {
                // 保存当前版本
                saveVersion(pageId, page.getConfig(), "回退前备份", null);
                // 恢复配置
                page.setConfig(targetVersion.getConfig());
                page.setVersion(page.getVersion() + 1);
                pageDao.updateById(page);
            }
        }
    }

    @Override
    public Map<String, Object> parseConfig(String config) {
        if (config == null || config.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(config, Map.class);
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
}
