package io.renren.modules.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 服务实现
 */
@Service
public class AiServiceImpl implements AiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    /**
     * 通用对话
     */
    @Override
    public String chat(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    /**
     * 生成页面 JSON
     * AI 根据描述返回页面组件结构
     */
    @Override
    public Map<String, Object> generatePage(String description) {
        String systemPrompt = """
            你是一个专业的网站布局助手。用户描述页面需求后，你需要返回页面组件JSON结构。
            
            返回格式要求：
            {
              "pageName": "页面名称",
              "components": [
                {
                  "id": "唯一ID",
                  "type": "组件类型",
                  "props": { "组件属性" }
                }
              ]
            }
            
            组件类型包括：
            - banner: 轮播图，需要 images(图片列表), autoPlay(是否自动播放)
            - text: 文本块，需要 content(文字内容), fontSize, color
            - image: 图片，需要 src(图片地址), alt, width, height
            - button: 按钮，需要 text(按钮文字), link(跳转链接), type(primary/secondary)
            - container: 布局容器，需要 direction(row/column), gap, children
            - productList: 产品列表，需要 title, products(产品数组[{name, price, image}])
            - footer: 底部，包含联系信息
            
            请根据用户需求生成合理的组件结构，只返回JSON，不要包含其他文字。
            """;

        String userPromptText = "页面需求：" + description + "\n\n请只返回JSON，不要有其他说明文字。";

        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(userPromptText)
        ));

        String response;
        try {
            ChatResponse chatResponse = chatModel.call(prompt);
            response = chatResponse.getResult().getOutput().getText();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "AI调用失败: " + e.getMessage());
            return error;
        }

        // 尝试解析 JSON
        response = response.trim();
        // 去掉可能的 markdown 代码块标记
        if (response.startsWith("```")) {
            response = response.substring(response.indexOf("\n") + 1);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.lastIndexOf("```"));
        }
        response = response.trim();

        try {
            return objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("error", "AI返回格式解析失败");
            fallback.put("raw", response);
            return fallback;
        }
    }

    /**
     * 生成代码 (Tailwind CSS + HTML)
     */
    @Override
    public String generateCode(String description, String framework) {
        String systemPrompt = """
            你是一个专业的网页开发者，擅长使用 Tailwind CSS 和 Vanilla JS/HTML 创建美观的页面。
            
            用户描述页面需求后，你需要生成完整的、可运行的 HTML 页面代码。
            
            要求：
            1. 使用 Tailwind CSS CDN（通过 CDN 安装）
            2. 使用 Vanilla JavaScript 处理交互
            3. 代码要完整、可直接运行
            4. 响应式设计，适配移动端
            5. 现代化、专业的视觉效果
            
            请只返回 HTML 代码，不要有其他说明文字。
            """;

        String userPromptText = "页面需求：" + description + "\n\n请只返回完整的 HTML 代码，使用 Tailwind CSS，不要有其他说明文字。";

        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(userPromptText)
        ));

        try {
            ChatResponse chatResponse = chatModel.call(prompt);
            String response = chatResponse.getResult().getOutput().getText();
            
            // 清理响应
            response = response.trim();
            if (response.startsWith("```html")) {
                response = response.substring(6);
            } else if (response.startsWith("```")) {
                response = response.substring(response.indexOf("\n") + 1);
            }
            if (response.endsWith("```")) {
                response = response.substring(0, response.lastIndexOf("```"));
            }
            
            return response.trim();
        } catch (Exception e) {
            throw new RuntimeException("AI 代码生成失败: " + e.getMessage());
        }
    }
}
