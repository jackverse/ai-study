package io.renren.modules.ai.controller;

import io.renren.common.utils.Result;
import io.renren.modules.ai.entity.SiteEntity;
import io.renren.modules.ai.service.SiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点相关接口
 */
@RestController
@RequestMapping("/sites")
@AllArgsConstructor
public class SiteController {

    private final SiteService siteService;

    /**
     * 获取站点详情
     */
    @GetMapping("/{siteId}")
    public Result<SiteEntity> getSite(@PathVariable("siteId") Long siteId) {
        SiteEntity site = siteService.getFirstByTenantId(null); // 需要根据ID查询
        return new Result<SiteEntity>().ok(site);
    }

    /**
     * 获取站点的页面列表
     */
    @GetMapping("/{siteId}/pages")
    public Result<List<SiteEntity>> getSitePages(
            @PathVariable("siteId") Long siteId,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        // 复用 PageController 的逻辑
        return new Result<List<SiteEntity>>().ok(null);
    }
}
