package io.renren.modules.saas.service;

/**
 * SaaS AI对话服务接口
 * 处理自然语言建站对话
 */
public interface SaaSAiChatService {

    /**
     * AI对话建站
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param message 用户消息
     * @return AI响应
     */
    String chat(Long tenantId, Long userId, String message);
}
