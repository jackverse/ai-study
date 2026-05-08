package io.renren.modules.ai.controller;

import io.renren.common.utils.Result;
import io.renren.modules.ai.entity.PageEntity;
import io.renren.modules.ai.entity.PageVersionEntity;
import io.renren.modules.ai.service.PageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 页面相关接口
 */
@RestController
@RequestMapping("/pages")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/{pageId}")
    public Result<PageEntity> getPage(@PathVariable("pageId") Long pageId) {
        PageEntity page = pageService.getById(pageId);
        if (page == null) {
            return new Result<PageEntity>().error("页面不存在");
        }
        return new Result<PageEntity>().ok(page);
    }

    @GetMapping("/sites/{siteId}/pages")
    public Result<List<PageEntity>> getSitePages(
            @PathVariable("siteId") Long siteId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        List<PageEntity> pages = pageService.getSitePages(tenantId, siteId);
        return new Result<List<PageEntity>>().ok(pages);
    }

    @PostMapping
    public Result<PageEntity> createPage(
            @RequestBody PageEntity page,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        page.setTenantId(tenantId);
        PageEntity created = pageService.create(page);
        return new Result<PageEntity>().ok(created);
    }

    @PutMapping("/{pageId}")
    public Result<Void> updatePage(
            @PathVariable("pageId") Long pageId,
            @RequestBody PageEntity page,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        page.setId(pageId);
        page.setTenantId(tenantId);
        pageService.update(page);
        return new Result<Void>().ok(null);
    }

    @DeleteMapping("/{pageId}")
    public Result<Void> deletePage(@PathVariable("pageId") Long pageId) {
        pageService.delete(pageId);
        return new Result<Void>().ok(null);
    }

    @PostMapping("/{pageId}/publish")
    public Result<Void> publishPage(@PathVariable("pageId") Long pageId) {
        pageService.publish(pageId);
        return new Result<Void>().ok(null);
    }

    @PostMapping("/{pageId}/versions")
    public Result<Void> saveVersion(
            @PathVariable("pageId") Long pageId,
            @RequestBody Map<String, String> params) {
        String config = params.get("config");
        String changeLog = params.get("changeLog");
        String createdBy = params.get("createdBy");
        pageService.saveVersion(pageId, config, changeLog, createdBy);
        return new Result<Void>().ok(null);
    }

    @GetMapping("/{pageId}/versions")
    public Result<List<PageVersionEntity>> getVersions(@PathVariable("pageId") Long pageId) {
        List<PageVersionEntity> versions = pageService.getVersionHistory(pageId);
        return new Result<List<PageVersionEntity>>().ok(versions);
    }

    @PostMapping("/{pageId}/rollback/{version}")
    public Result<Void> rollback(
            @PathVariable("pageId") Long pageId,
            @PathVariable("version") Integer version) {
        pageService.rollback(pageId, version);
        return new Result<Void>().ok(null);
    }
}
