package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.common.utils.Result;
import io.renren.entity.SiteEntity;
import io.renren.service.SiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点管理接口
 */
@RestController
@RequestMapping("/api/sites")
@AllArgsConstructor
public class SiteController {

    private final SiteService siteService;

    /**
     * 获取站点列表
     */
    @Login
    @GetMapping
    public Result<List<SiteEntity>> list(@RequestAttribute("userId") Long userId) {
        List<SiteEntity> list = siteService.getListByUserId(userId);
        return new Result<List<SiteEntity>>().ok(list);
    }

    /**
     * 获取站点详情
     */
    @Login
    @GetMapping("/{id}")
    public Result<SiteEntity> get(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        SiteEntity site = siteService.getByIdAndUserId(id, userId);
        if (site == null) {
            return new Result<SiteEntity>().error("站点不存在");
        }
        return new Result<SiteEntity>().ok(site);
    }

    /**
     * 创建站点
     */
    @Login
    @PostMapping
    public Result<SiteEntity> create(
            @RequestBody SiteEntity site,
            @RequestAttribute("userId") Long userId) {
        SiteEntity created = siteService.create(site, userId);
        return new Result<SiteEntity>().ok(created);
    }

    /**
     * 更新站点
     */
    @Login
    @PutMapping("/{id}")
    public Result<SiteEntity> update(
            @PathVariable("id") Long id,
            @RequestBody SiteEntity site,
            @RequestAttribute("userId") Long userId) {
        site.setId(id);
        SiteEntity updated = siteService.update(site, userId);
        return new Result<SiteEntity>().ok(updated);
    }

    /**
     * 删除站点
     */
    @Login
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        siteService.deleteByIdAndUserId(id, userId);
        return new Result<Void>().ok(null);
    }

    /**
     * 发布站点
     */
    @Login
    @PostMapping("/{id}/publish")
    public Result<Map<String, Object>> publish(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        SiteEntity site = siteService.getByIdAndUserId(id, userId);
        if (site == null) {
            return new Result<Map<String, Object>>().error("站点不存在");
        }
        // TODO: 实际发布逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("url", "https://" + site.getSubdomain() + ".example.com");
        return new Result<Map<String, Object>>().ok(result);
    }
}
