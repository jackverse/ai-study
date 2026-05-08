package io.renren.modules.saas.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 建站模板表 (SiteTemplate)
 * 预设的网站模板，用户可基于模板快速建站
 */
@Data
@TableName("site_template")
public class SiteTemplateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 模板名称 */
    private String name;
    
    /** 模板编码 */
    private String code;
    
    /** 模板分类: enterprise/mall/blog/landing */
    private String category;
    
    /** 模板描述 */
    private String description;
    
    /** 缩略图URL */
    private String thumbnail;
    
    /** 模板预览URL */
    private String previewUrl;
    
    /** 模板配置 JSON: 组件列表/布局等 */
    private String config;
    
    /** 是否付费: 0-免费, 1-付费 */
    private Integer paid;
    
    /** 价格 (分为单位) */
    private Integer price;
    
    /** 排序 */
    private Integer sort;
    
    /** 状态: 0-禁用, 1-启用 */
    private Integer status;
    
    /** 使用次数 */
    private Integer useCount;
    
    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
