package io.renren.modules.ai.controller;

import io.renren.common.utils.Result;
import io.renren.modules.ai.entity.SiteEntity;
import io.renren.modules.ai.entity.TenantEntity;
import io.renren.modules.ai.service.SiteService;
import io.renren.modules.ai.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户相关接口
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService tenantService;
    private final SiteService siteService;

    public TenantController(TenantService tenantService, SiteService siteService) {
        this.tenantService = tenantService;
        this.siteService = siteService;
    }

    @GetMapping("/{tenantId}")
    public Result<TenantEntity> getTenantInfo(@PathVariable("tenantId") String tenantId) {
        TenantEntity tenant = tenantService.getByTenantId(tenantId);
        if (tenant == null) {
            return new Result<TenantEntity>().error("租户不存在");
        }
        return new Result<TenantEntity>().ok(tenant);
    }

    @GetMapping("/{tenantId}/site")
    public Result<SiteEntity> getTenantSite(@PathVariable("tenantId") String tenantId) {
        SiteEntity site = siteService.getFirstByTenantId(tenantId);
        if (site == null) {
            return new Result<SiteEntity>().error("该租户暂无站点");
        }
        return new Result<SiteEntity>().ok(site);
    }

    @GetMapping("/{tenantId}/sites")
    public Result<List<SiteEntity>> getTenantSites(@PathVariable("tenantId") String tenantId) {
        List<SiteEntity> sites = siteService.getByTenantId(tenantId);
        return new Result<List<SiteEntity>>().ok(sites);
    }
}
