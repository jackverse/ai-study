-- =============================================
-- AI 建站 MVP 数据库表结构
-- =============================================

-- 站点表
CREATE TABLE IF NOT EXISTS `site` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `name` VARCHAR(128) NOT NULL COMMENT '站点名称',
  `description` VARCHAR(512) COMMENT '站点描述',
  `subdomain` VARCHAR(64) COMMENT '子域名',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_subdomain` (`subdomain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点表';

-- 页面表
CREATE TABLE IF NOT EXISTS `page` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `site_id` BIGINT NOT NULL COMMENT '所属站点ID',
  `name` VARCHAR(128) NOT NULL COMMENT '页面名称',
  `path` VARCHAR(128) COMMENT '页面路径',
  `config` JSON COMMENT '页面配置JSON',
  `version` INT DEFAULT 1 COMMENT '版本号',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0草稿 1发布',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面表';

-- 页面版本表
CREATE TABLE IF NOT EXISTS `page_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_id` BIGINT NOT NULL COMMENT '页面ID',
  `version` INT NOT NULL COMMENT '版本号',
  `config` JSON NOT NULL COMMENT '配置快照',
  `change_log` VARCHAR(512) COMMENT '变更说明',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`page_id`),
  UNIQUE KEY `uk_page_version` (`page_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面版本表';

-- =============================================
-- 初始化测试用户
-- =============================================

-- 测试用户 (密码是 admin123，需要先注册)
-- INSERT INTO `sys_user` (`username`, `password`, `status`, `create_time`) 
-- VALUES ('test', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxx', 1, NOW());
