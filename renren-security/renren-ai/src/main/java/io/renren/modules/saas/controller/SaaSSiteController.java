package io.renren.modules.saas.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.utils.Result;
import io.renren.modules.saas.entity.SiteEntity;
import io.renren.modules.saas.entity.SitePageEntity;
import io.renren.modules.saas.entity.SiteTemplateEntity;
import io.renren.modules.saas.service.SaaSPageService;
import io.renren.modules.saas.service.SaaSSiteService;
import io.renren.modules.saas.service.SaaSTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SaaS站点管理控制器
 * 
 * 合并来源:
 * - renren-ai: io.renren.modules.ai.controller.SiteController (原有)
 * - renren-saas-builder: com.openclaw.saas.controller.site.SiteController (扩展)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/saas/sites")
@Tag(name = "SaaS站点管理", description = "站点的CRUD管理")
public class SaaSSiteController {

    private final SaaSSiteService siteService;
    private final SaaSPageService pageService;
    private final SaaSTemplateService templateService;

    // ========== 站点管理 ==========

    @PostMapping
    @Operation(summary = "创建站点")
    public Result<SiteEntity> create(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String description = params.get("description");
        String category = params.get("category");
        SiteEntity site = siteService.createSite(name, description, category);
        return new Result<SiteEntity>().ok(site);
    }

    @PutMapping("/{siteId}")
    @Operation(summary = "更新站点")
    public Result<SiteEntity> update(@PathVariable Long siteId, @RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String config = params.get("config") != null ? params.get("config").toString() : null;
        SiteEntity site = siteService.updateSite(siteId, name, config);
        return new Result<SiteEntity>().ok(site);
    }

    @PostMapping("/{siteId}/publish")
    @Operation(summary = "发布站点")
    public Result<SiteEntity> publish(@PathVariable Long siteId) {
        SiteEntity site = siteService.publishSite(siteId);
        return new Result<SiteEntity>().ok(site);
    }

    @GetMapping
    @Operation(summary = "站点列表")
    public Result<IPage<SiteEntity>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SiteEntity> page = siteService.getSites(pageNum, pageSize);
        return new Result<IPage<SiteEntity>>().ok(page);
    }

    @GetMapping("/{siteId}")
    @Operation(summary = "站点详情")
    public Result<SiteEntity> get(@PathVariable Long siteId) {
        SiteEntity site = siteService.getSiteInfo(siteId);
        return new Result<SiteEntity>().ok(site);
    }

    @PostMapping("/{siteId}/template/{templateId}")
    @Operation(summary = "应用模板到站点")
    public Result<Void> applyTemplate(@PathVariable Long siteId, @PathVariable Long templateId) {
        siteService.applyTemplate(siteId, templateId);
        return new Result<Void>().ok(null);
    }

    // ========== 页面管理 ==========

    @PostMapping("/{siteId}/pages")
    @Operation(summary = "创建页面")
    public Result<SitePageEntity> createPage(@PathVariable Long siteId, @RequestBody Map<String, String> params) {
        String name = params.get("name");
        String title = params.get("title");
        String path = params.get("path");
        String pageType = params.get("pageType");
        SitePageEntity page = pageService.createPage(siteId, name, title, path, pageType);
        return new Result<SitePageEntity>().ok(page);
    }

    @GetMapping("/{siteId}/pages")
    @Operation(summary = "获取站点页面列表")
    public Result<List<SitePageEntity>> getPages(@PathVariable Long siteId) {
        List<SitePageEntity> pages = pageService.getPagesBySite(siteId);
        return new Result<List<SitePageEntity>>().ok(pages);
    }

    @PutMapping("/pages/{pageId}")
    @Operation(summary = "更新页面")
    public Result<SitePageEntity> updatePage(@PathVariable Long pageId, @RequestBody Map<String, Object> params) {
        String layout = params.get("layout") != null ? params.get("layout").toString() : null;
        String content = params.get("content") != null ? params.get("content").toString() : null;
        String seoTitle = (String) params.get("seoTitle");
        String seoDescription = (String) params.get("seoDescription");
        SitePageEntity page = pageService.updatePageContent(pageId, layout, content, seoTitle, seoDescription);
        return new Result<SitePageEntity>().ok(page);
    }

    @DeleteMapping("/pages/{pageId}")
    @Operation(summary = "删除页面")
    public Result<Void> deletePage(@PathVariable Long pageId) {
        pageService.deletePage(pageId);
        return new Result<Void>().ok(null);
    }

    // ========== 模板管理 ==========

    @GetMapping("/templates")
    @Operation(summary = "模板列表")
    public Result<IPage<SiteTemplateEntity>> templates(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SiteTemplateEntity> page = templateService.getTemplates(category, pageNum, pageSize);
        return new Result<IPage<SiteTemplateEntity>>().ok(page);
    }

    @GetMapping("/templates/{templateId}")
    @Operation(summary = "模板详情")
    public Result<SiteTemplateEntity> getTemplate(@PathVariable Long templateId) {
        SiteTemplateEntity template = templateService.getTemplateById(templateId);
        return new Result<SiteTemplateEntity>().ok(template);
    }
}
