package io.renren.modules.saas.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站点页面表 (SitePage)
 * 每个站点下的页面，支持AI生成
 * 
 * 合并来源:
 * - renren-ai: io.renren.modules.ai.entity.PageEntity (原有字段)
 * - renren-saas-builder: com.openclaw.saas.model.site.SitePage (扩展字段)
 */
@Data
@TableName("site_page")
public class SitePageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 站点ID */
    private Long siteId;
    
    /** 页面名称 */
    private String name;
    
    /** 页面标题 */
    private String title;
    
    /** 页面路径 URL */
    private String path;
    
    /** 页面类型: static/dynamic/ai-generated (新增) */
    private String pageType;
    
    /** 页面布局 JSON格式的组件树 (新增) */
    private String layout;
    
    /** 页面内容 HTML/JSON (原有config字段保留) */
    private String content;
    
    /** SEO标题 (原有) */
    private String seoTitle;
    
    /** SEO关键词 (原有) */
    private String seoKeywords;
    
    /** SEO描述 (新增 from saas-builder) */
    private String seoDescription;
    
    /** 页面版本号 (原有) */
    private Integer version;
    
    /** 是否首页 (原有) */
    private Integer isHome;
    
    /** 页面排序 (新增) */
    private Integer sort;
    
    /** 状态: 0-草稿, 1-已发布 (合并后统一) */
    private Integer status;
    
    /** AI生成标记: 0-手动, 1-AI生成 (新增) */
    private Integer aiGenerated;
    
    /** AI对话ID 关联AI对话历史 (新增) */
    private String aiConversationId;
    
    /** 发布时间 (原有) */
    private LocalDateTime publishedAt;
    
    /** 创建者 (新增) */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;
    
    /** 创建时间 (新增) */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新者 (新增) */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    
    /** 更新时间 (新增) */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
