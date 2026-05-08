package io.renren.modules.saas.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 站点表 (Site)
 * 租户下的每个网站
 * 
 * 合并来源:
 * - renren-ai: io.renren.modules.ai.entity.SiteEntity (原有字段)
 * - renren-saas-builder: com.openclaw.saas.model.site.Site (扩展字段)
 */
@Data
@TableName("site")
public class SiteEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 租户ID (原renren-ai) */
    private String tenantId;
    
    /** 站点名称 (合并后统一) */
    private String siteName;
    
    /** 站点编码 (原renren-ai) */
    private String siteCode;
    
    /** 站点描述 */
    private String description;
    
    /** 站点域名/路径 (新增) */
    private String domain;
    
    /** 使用的模板ID (新增) */
    private Long templateId;
    
    /** 站点配置 JSON: 主题/配色/字体等 (新增) */
    private String config;
    
    /** 站点状态: 0-建设中, 1-已发布, 2-已下线 */
    private Integer status;
    
    /** 是否使用AI辅助: 0-否, 1-是 (新增) */
    private Integer aiEnabled;
    
    /** 访问量 (新增) */
    private Long visitCount;
    
    /** 发布时间 (原renren-ai) */
    private Date publishedAt;
    
    /** 创建时间 */
    private Date createdAt;
    
    /** 更新时间 */
    private Date updatedAt;
    
    /** 创建者 (新增) */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;
    
    /** 更新者 (新增) */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
