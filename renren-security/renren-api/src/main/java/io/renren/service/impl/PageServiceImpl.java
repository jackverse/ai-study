package io.renren.service.impl;

import io.renren.common.service.CrudService;
import io.renren.dao.PageDao;
import io.renren.entity.PageEntity;
import io.renren.service.PageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PageServiceImpl extends CrudService<PageDao, PageEntity> implements PageService {

    @Override
    public List<PageEntity> getListBySiteId(Long siteId) {
        return baseDao.selectBySiteId(siteId);
    }

    @Override
    public PageEntity getByIdAndUserId(Long id, Long userId) {
        List<PageEntity> list = baseDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PageEntity>()
                .eq("id", id)
                .eq("user_id", userId)
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        baseDao.delete(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PageEntity>()
                .eq("id", id)
                .eq("user_id", userId)
        );
    }

    @Override
    public PageEntity create(PageEntity page, Long userId) {
        page.setUserId(userId);
        page.setCreatedAt(new Date());
        page.setUpdatedAt(new Date());
        page.setVersion(1);
        page.setStatus(0);
        baseDao.insert(page);
        return page;
    }

    @Override
    public PageEntity update(PageEntity page, Long userId) {
        PageEntity exist = getByIdAndUserId(page.getId(), userId);
        if (exist == null) {
            throw new RuntimeException("页面不存在或无权修改");
        }
        page.setUserId(userId);
        page.setUpdatedAt(new Date());
        page.setVersion(exist.getVersion() + 1);
        baseDao.updateById(page);
        return page;
    }
}
