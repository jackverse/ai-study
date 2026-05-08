package io.renren.modules.ai.service;

import java.util.Map;

/**
 * AI 服务接口
 */
public interface AiService {

    /**
     * 通用对话
     */
    String chat(String message);

    /**
     * 生成页面 JSON
     */
    Map<String, Object> generatePage(String description);

    /**
     * 生成代码 (Tailwind CSS + HTML)
     */
    String generateCode(String description, String framework);
}
