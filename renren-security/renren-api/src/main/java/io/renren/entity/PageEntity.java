package io.renren.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("page")
public class PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long siteId;
    
    private String name;
    
    private String path;
    
    private String config;
    
    private Integer version;
    
    private Integer status;
    
    private Date createdAt;
    
    private Date updatedAt;
}
