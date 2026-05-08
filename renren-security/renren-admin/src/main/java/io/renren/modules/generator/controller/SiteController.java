package io.renren.modules.generator.controller;

import io.renren.common.utils.Result;
import io.renren.modules.generator.dao.SiteDao;
import io.renren.modules.generator.entity.SiteEntity;
import io.renren.modules.security.user.SecurityUser;
import io.renren.modules.security.user.UserDetail;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generator/sites")
@AllArgsConstructor
public class SiteController {

    private final SiteDao siteDao;

    @GetMapping
    public Result<List<SiteEntity>> list() {
        UserDetail user = SecurityUser.getUser();
        List<SiteEntity> list = siteDao.selectByUserId(user.getId());
        return new Result<List<SiteEntity>>().ok(list);
    }

    @GetMapping("/{id}")
    public Result<SiteEntity> get(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        SiteEntity site = siteDao.selectByIdAndUserId(id, user.getId());
        if (site == null) {
            return new Result<SiteEntity>().error("站点不存在");
        }
        return new Result<SiteEntity>().ok(site);
    }

    @PostMapping
    public Result<SiteEntity> create(@RequestBody SiteEntity site) {
        UserDetail user = SecurityUser.getUser();
        site.setUserId(user.getId());
        site.setCreatedAt(new Date());
        site.setUpdatedAt(new Date());
        site.setStatus(1);
        siteDao.insert(site);
        return new Result<SiteEntity>().ok(site);
    }

    @PutMapping("/{id}")
    public Result<SiteEntity> update(@PathVariable("id") Long id, @RequestBody SiteEntity site) {
        UserDetail user = SecurityUser.getUser();
        SiteEntity exist = siteDao.selectByIdAndUserId(id, user.getId());
        if (exist == null) {
            return new Result<SiteEntity>().error("站点不存在");
        }
        site.setId(id);
        site.setUserId(user.getId());
        site.setUpdatedAt(new Date());
        siteDao.updateById(site);
        return new Result<SiteEntity>().ok(site);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        SiteEntity exist = siteDao.selectByIdAndUserId(id, user.getId());
        if (exist == null) {
            return new Result<Void>().error("站点不存在");
        }
        siteDao.deleteById(id);
        return new Result<Void>().ok(null);
    }

    @PostMapping("/{id}/publish")
    public Result<Map<String, Object>> publish(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        SiteEntity site = siteDao.selectByIdAndUserId(id, user.getId());
        if (site == null) {
            return new Result<Map<String, Object>>().error("站点不存在");
        }
        // TODO: 实际发布逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("url", "https://" + (site.getSubdomain() != null ? site.getSubdomain() : "site-" + id) + ".example.com");
        result.put("published", true);
        return new Result<Map<String, Object>>().ok(result);
    }
}
