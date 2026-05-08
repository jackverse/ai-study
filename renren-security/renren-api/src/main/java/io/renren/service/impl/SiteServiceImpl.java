package io.renren.service.impl;

import io.renren.common.service.CrudService;
import io.renren.dao.SiteDao;
import io.renren.entity.SiteEntity;
import io.renren.service.SiteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class SiteServiceImpl extends CrudService<SiteDao, SiteEntity> implements SiteService {

    @Override
    public List<SiteEntity> getListByUserId(Long userId) {
        return baseDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SiteEntity>()
                .eq("user_id", userId)
                .orderByDesc("created_at")
        );
    }

    @Override
    public SiteEntity getByIdAndUserId(Long id, Long userId) {
        List<SiteEntity> list = baseDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SiteEntity>()
                .eq("id", id)
                .eq("user_id", userId)
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        baseDao.delete(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SiteEntity>()
                .eq("id", id)
                .eq("user_id", userId)
        );
    }

    @Override
    public SiteEntity create(SiteEntity site, Long userId) {
        site.setUserId(userId);
        site.setCreatedAt(new Date());
        site.setUpdatedAt(new Date());
        site.setStatus(1);
        baseDao.insert(site);
        return site;
    }

    @Override
    public SiteEntity update(SiteEntity site, Long userId) {
        SiteEntity exist = getByIdAndUserId(site.getId(), userId);
        if (exist == null) {
            throw new RuntimeException("站点不存在或无权修改");
        }
        site.setUserId(userId);
        site.setUpdatedAt(new Date());
        baseDao.updateById(site);
        return site;
    }
}
