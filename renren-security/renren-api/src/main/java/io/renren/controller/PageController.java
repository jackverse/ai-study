package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.common.utils.Result;
import io.renren.entity.PageEntity;
import io.renren.service.PageService;
import io.renren.service.SiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 页面管理接口
 */
@RestController
@RequestMapping("/api/pages")
@AllArgsConstructor
public class PageController {

    private final PageService pageService;
    private final SiteService siteService;

    /**
     * 获取站点的页面列表
     */
    @Login
    @GetMapping("/sites/{siteId}")
    public Result<List<PageEntity>> listBySite(@PathVariable("siteId") Long siteId) {
        List<PageEntity> list = pageService.getListBySiteId(siteId);
        return new Result<List<PageEntity>>().ok(list);
    }

    /**
     * 获取页面详情
     */
    @Login
    @GetMapping("/{id}")
    public Result<PageEntity> get(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        PageEntity page = pageService.getByIdAndUserId(id, userId);
        if (page == null) {
            return new Result<PageEntity>().error("页面不存在");
        }
        return new Result<PageEntity>().ok(page);
    }

    /**
     * 创建页面
     */
    @Login
    @PostMapping
    public Result<PageEntity> create(
            @RequestBody PageEntity page,
            @RequestAttribute("userId") Long userId) {
        // 验证站点归属
        var site = siteService.getByIdAndUserId(page.getSiteId(), userId);
        if (site == null) {
            return new Result<PageEntity>().error("站点不存在或无权访问");
        }
        PageEntity created = pageService.create(page, userId);
        return new Result<PageEntity>().ok(created);
    }

    /**
     * 更新页面
     */
    @Login
    @PutMapping("/{id}")
    public Result<PageEntity> update(
            @PathVariable("id") Long id,
            @RequestBody PageEntity page,
            @RequestAttribute("userId") Long userId) {
        page.setId(id);
        PageEntity updated = pageService.update(page, userId);
        return new Result<PageEntity>().ok(updated);
    }

    /**
     * 删除页面
     */
    @Login
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        pageService.deleteByIdAndUserId(id, userId);
        return new Result<Void>().ok(null);
    }

    /**
     * 发布页面
     */
    @Login
    @PostMapping("/{id}/publish")
    public Result<Void> publish(
            @PathVariable("id") Long id,
            @RequestAttribute("userId") Long userId) {
        PageEntity page = pageService.getByIdAndUserId(id, userId);
        if (page == null) {
            return new Result<Void>().error("页面不存在");
        }
        page.setStatus(1);
        pageService.update(page, userId);
        return new Result<Void>().ok(null);
    }
}
