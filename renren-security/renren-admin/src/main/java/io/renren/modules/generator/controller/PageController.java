package io.renren.modules.generator.controller;

import io.renren.common.utils.Result;
import io.renren.modules.generator.dao.PageDao;
import io.renren.modules.generator.dao.SiteDao;
import io.renren.modules.generator.entity.PageEntity;
import io.renren.modules.generator.entity.SiteEntity;
import io.renren.modules.security.user.SecurityUser;
import io.renren.modules.security.user.UserDetail;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/generator/pages")
@AllArgsConstructor
public class PageController {

    private final PageDao pageDao;
    private final SiteDao siteDao;

    @GetMapping("/sites/{siteId}")
    public Result<List<PageEntity>> listBySite(@PathVariable("siteId") Long siteId) {
        UserDetail user = SecurityUser.getUser();
        // 验证站点归属
        SiteEntity site = siteDao.selectByIdAndUserId(siteId, user.getId());
        if (site == null) {
            return new Result<List<PageEntity>>().error("站点不存在");
        }
        List<PageEntity> list = pageDao.selectBySiteId(siteId);
        return new Result<List<PageEntity>>().ok(list);
    }

    @GetMapping("/{id}")
    public Result<PageEntity> get(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        PageEntity page = pageDao.selectByIdAndUserId(id, user.getId());
        if (page == null) {
            return new Result<PageEntity>().error("页面不存在");
        }
        return new Result<PageEntity>().ok(page);
    }

    @PostMapping
    public Result<PageEntity> create(@RequestBody PageEntity page) {
        UserDetail user = SecurityUser.getUser();
        // 验证站点归属
        SiteEntity site = siteDao.selectByIdAndUserId(page.getSiteId(), user.getId());
        if (site == null) {
            return new Result<PageEntity>().error("站点不存在");
        }
        page.setUserId(user.getId());
        page.setCreatedAt(new Date());
        page.setUpdatedAt(new Date());
        page.setVersion(1);
        page.setStatus(0);
        pageDao.insert(page);
        return new Result<PageEntity>().ok(page);
    }

    @PutMapping("/{id}")
    public Result<PageEntity> update(@PathVariable("id") Long id, @RequestBody PageEntity page) {
        UserDetail user = SecurityUser.getUser();
        PageEntity exist = pageDao.selectByIdAndUserId(id, user.getId());
        if (exist == null) {
            return new Result<PageEntity>().error("页面不存在");
        }
        page.setId(id);
        page.setUserId(user.getId());
        page.setUpdatedAt(new Date());
        page.setVersion(exist.getVersion() + 1);
        pageDao.updateById(page);
        return new Result<PageEntity>().ok(page);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        PageEntity exist = pageDao.selectByIdAndUserId(id, user.getId());
        if (exist == null) {
            return new Result<Void>().error("页面不存在");
        }
        pageDao.deleteById(id);
        return new Result<Void>().ok(null);
    }

    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        PageEntity page = pageDao.selectByIdAndUserId(id, user.getId());
        if (page == null) {
            return new Result<Void>().error("页面不存在");
        }
        page.setStatus(1);
        page.setUpdatedAt(new Date());
        pageDao.updateById(page);
        return new Result<Void>().ok(null);
    }
}
