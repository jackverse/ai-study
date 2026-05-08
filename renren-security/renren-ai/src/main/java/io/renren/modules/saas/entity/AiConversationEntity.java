package io.renren.modules.saas.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话记录表 (AiConversation)
 * 记录用户在AI辅助建站过程中的对话历史
 */
@Data
@TableName("ai_conversation")
public class AiConversationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 租户ID */
    private Long tenantId;
    
    /** 站点ID */
    private Long siteId;
    
    /** 页面ID (可选，关联特定页面) */
    private Long pageId;
    
    /** 对话会话ID (UUID) */
    private String conversationId;
    
    /** 对话轮次 */
    private Integer turn;
    
    /** 用户输入 */
    private String userMessage;
    
    /** AI响应 */
    private String aiMessage;
    
    /** AI执行的操作 JSON: 包含tool_calls等 */
    private String aiActions;
    
    /** 操作结果 */
    private String result;
    
    /** 是否成功 */
    private Integer success;
    
    /** 错误信息 */
    private String errorMsg;
    
    /** 消耗token数 */
    private Integer tokenUsed;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
