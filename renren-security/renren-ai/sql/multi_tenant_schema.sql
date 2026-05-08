-- =============================================
-- 多租户 AI 建站系统 - 数据库表结构
-- =============================================

-- 1. 租户表
CREATE TABLE IF NOT EXISTS `tenant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` VARCHAR(64) NOT NULL COMMENT '租户唯一标识',
  `tenant_name` VARCHAR(128) NOT NULL COMMENT '租户名称',
  `domain` VARCHAR(256) COMMENT '访问域名',
  `logo_url` VARCHAR(512) COMMENT '租户Logo',
  `ai_quota` INT DEFAULT 1000 COMMENT 'AI配额次数',
  `ai_used` INT DEFAULT 0 COMMENT '已使用次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `expire_time` DATETIME COMMENT '过期时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`),
  KEY `idx_domain` (`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 2. 站点表
CREATE TABLE IF NOT EXISTS `site` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` VARCHAR(64) NOT NULL COMMENT '所属租户',
  `site_code` VARCHAR(64) NOT NULL COMMENT '站点代码(URL用)',
  `site_name` VARCHAR(128) NOT NULL COMMENT '站点名称',
  `description` VARCHAR(512) COMMENT '站点描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0下架 1正常',
  `published_at` DATETIME COMMENT '发布时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_tenant_site` (`tenant_id`, `site_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_site_code` (`site_code`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点表';

-- 3. 页面表
CREATE TABLE IF NOT EXISTS `page` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` VARCHAR(64) NOT NULL COMMENT '所属租户',
  `site_id` BIGINT NOT NULL COMMENT '所属站点',
  `page_name` VARCHAR(128) NOT NULL COMMENT '页面名称',
  `page_path` VARCHAR(128) COMMENT '页面路径 如 /index',
  `config` JSON COMMENT '页面配置JSON',
  `seo_title` VARCHAR(256) COMMENT 'SEO标题',
  `seo_keywords` VARCHAR(512) COMMENT 'SEO关键词',
  `seo_description` VARCHAR(1024) COMMENT 'SEO描述',
  `version` INT DEFAULT 1 COMMENT '当前版本号',
  `is_home` TINYINT DEFAULT 0 COMMENT '是否首页',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0草稿 1发布',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_site_id` (`site_id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面表';

-- 4. 页面版本表
CREATE TABLE IF NOT EXISTS `page_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_id` BIGINT NOT NULL COMMENT '页面ID',
  `version` INT NOT NULL COMMENT '版本号',
  `config` JSON NOT NULL COMMENT '配置快照',
  `change_log` VARCHAR(512) COMMENT '变更说明',
  `created_by` VARCHAR(64) COMMENT '创建人',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_page_version` (`page_id`, `version`),
  KEY `idx_page_id` (`page_id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面版本表';

-- 5. 组件模板表
CREATE TABLE IF NOT EXISTS `component_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` VARCHAR(64) NOT NULL COMMENT '所属租户',
  `category` VARCHAR(32) NOT NULL COMMENT '分类 nav/banner/text...',
  `name` VARCHAR(128) NOT NULL COMMENT '模板名称',
  `thumbnail` VARCHAR(512) COMMENT '缩略图URL',
  `config` JSON NOT NULL COMMENT '模板配置JSON',
  `is_public` TINYINT DEFAULT 0 COMMENT '是否公共模板',
  `use_count` INT DEFAULT 0 COMMENT '使用次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_tenant_category` (`tenant_id`, `category`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组件模板表';

-- 6. AI 生成历史表
CREATE TABLE IF NOT EXISTS `ai_generation_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` VARCHAR(64) NOT NULL COMMENT '所属租户',
  `site_id` BIGINT COMMENT '关联站点',
  `page_id` BIGINT COMMENT '关联页面',
  `prompt` TEXT NOT NULL COMMENT '用户输入',
  `ai_response` TEXT COMMENT 'AI响应',
  `generated_config` JSON COMMENT '生成的配置',
  `model` VARCHAR(64) COMMENT 'AI模型',
  `tokens_used` INT DEFAULT 0 COMMENT '消耗token',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功',
  `error_msg` VARCHAR(512) COMMENT '错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_tenant_time` (`tenant_id`, `created_at`),
  KEY `idx_site_id` (`site_id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI生成历史表';

-- =============================================
-- 初始化测试数据
-- =============================================

-- 插入测试租户
INSERT INTO `tenant` (`tenant_id`, `tenant_name`, `domain`, `ai_quota`, `status`) VALUES
('tenant_demo', '演示租户', 'tenant-demo.ztpy.com', 1000, 1),
('tenant_a', '租户A公司', 'tenant-a.ztpy.com', 5000, 1),
('tenant_b', '租户B公司', 'tenant-b.ztpy.com', 3000, 1);

-- 插入测试站点
INSERT INTO `site` (`tenant_id`, `site_code`, `site_name`, `description`, `status`) VALUES
('tenant_demo', 'demo', '演示站点', '用于功能演示的站点', 1),
('tenant_a', 'company_a', 'A公司官网', 'A公司官方网站', 1),
('tenant_a', 'product_a', 'A公司产品站', 'A公司产品展示站', 1),
('tenant_b', 'company_b', 'B公司官网', 'B公司官方网站', 1);

-- 插入测试页面
INSERT INTO `page` (`tenant_id`, `site_id`, `page_name`, `page_path`, `config`, `is_home`, `status`) VALUES
('tenant_demo', 1, '首页', '/index', '{"components":[]}', 1, 1),
('tenant_demo', 1, '关于我们', '/about', '{"components":[]}', 0, 1),
('tenant_a', 2, '首页', '/index', '{"components":[]}', 1, 1),
('tenant_a', 2, '产品中心', '/products', '{"components":[]}', 0, 1),
('tenant_a', 3, '首页', '/index', '{"components":[]}', 1, 1),
('tenant_b', 4, '首页', '/index', '{"components":[]}', 1, 1);
