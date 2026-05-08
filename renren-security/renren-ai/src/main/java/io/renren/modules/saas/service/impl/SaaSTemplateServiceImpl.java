package io.renren.modules.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.saas.dao.SiteTemplateDao;
import io.renren.modules.saas.entity.SiteTemplateEntity;
import io.renren.modules.saas.service.SaaSTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SaaS模板管理服务实现
 */
@Slf4j
@Service
public class SaaSTemplateServiceImpl extends ServiceImpl<SiteTemplateDao, SiteTemplateEntity> implements SaaSTemplateService {

    @Override
    public Page<SiteTemplateEntity> getTemplates(String category, Integer pageNum, Integer pageSize) {
        Page<SiteTemplateEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SiteTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteTemplateEntity::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(SiteTemplateEntity::getCategory, category);
        }
        wrapper.orderByAsc(SiteTemplateEntity::getSort);
        return this.page(page, wrapper);
    }

    @Override
    public SiteTemplateEntity getTemplateById(Long templateId) {
        return this.getById(templateId);
    }

    @Override
    public void applyTemplateToSite(Long siteId, Long templateId) {
        SiteTemplateEntity template = this.getById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        log.info("模板应用到站点 - siteId: {}, templateId: {}, templateName: {}", 
                 siteId, templateId, template.getName());
    }
}
