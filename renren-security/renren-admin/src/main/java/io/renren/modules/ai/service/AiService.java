package io.renren.modules.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.modules.ai.config.AiConfig;
import io.renren.modules.ai.dto.ImageGenerateRequest;
import io.renren.modules.ai.dto.TtsRequest;
import io.renren.modules.ai.dto.VideoGenerateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class AiService {

    private final AiConfig aiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(AiConfig aiConfig, RestTemplate restTemplate) {
        this.aiConfig = aiConfig;
        this.restTemplate = restTemplate;
    }

    public String chat(String message) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 2048);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", List.of(Map.of("type", "text", "text", message)));
        messages.add(userMessage);
        requestBody.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", aiConfig.getApiKey());
        headers.set("anthropic-version", "2023-06-01");
        
        String url = aiConfig.getBaseUrl() + "/v1/messages";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            return parseAnthropicResponse(response.getBody());
        } catch (Exception e) {
            log.error("AI 调用失败：", e);
            return "AI 调用失败: " + e.getMessage();
        }
    }

    public Map<String, Object> generatePage(String description) {
        Map<String, Object> result = new HashMap<>();
        
        String systemPrompt = "你是一个专业的网站布局助手。用户描述页面需求后，你需要返回页面组件JSON结构。\n" +
            "重要：只返回JSON格式，不要有其他任何文字说明。\n" +
            "返回格式：\n" +
            "{\n" +
            "  \"pageName\": \"页面名称\",\n" +
            "  \"components\": [\n" +
            "    {\n" +
            "      \"id\": \"组件ID\",\n" +
            "      \"type\": \"组件类型\",\n" +
            "      \"props\": {}\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "组件类型（只能使用这些）：banner, text, image, button, container, productList, footer, navigation, search, contactForm, teamSection, pricingTable, testimonials\n" +
            "页面结构要完整，components数组必须有结束符]。";
        
        String userPrompt = "需求：" + description;
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 4096);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", List.of(Map.of("type", "text", "text", userPrompt)));
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("system", systemPrompt);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", aiConfig.getApiKey());
        headers.set("anthropic-version", "2023-06-01");
        
        String url = aiConfig.getBaseUrl() + "/v1/messages";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("页面生成请求 URL: {}", url);
            log.info("页面生成请求 Body: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            log.info("页面生成响应: {}", response.getBody());
            
            String content = parseAnthropicResponse(response.getBody());
            if (content != null && !content.isEmpty()) {
                return parseJsonResponse(content);
            } else {
                result.put("error", "无法解析 AI 响应内容为空");
                result.put("raw", response.getBody());
                return result;
            }
        } catch (Exception e) {
            log.error("AI 调用失败：", e);
            result.put("error", "AI调用失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 生成代码 (Tailwind CSS + HTML)
     */
    public Map<String, Object> generateCode(String description, String framework) {
        Map<String, Object> result = new HashMap<>();
        
        String systemPrompt = "你是一个专业的网页开发者，擅长使用 Tailwind CSS 和 HTML 创建美观的页面。\n" +
            "用户描述页面需求后，你需要生成完整的、可运行的 HTML 页面代码。\n" +
            "\n" +
            "要求：\n" +
            "1. 使用 Tailwind CSS CDN（通过 CDN 安装）\n" +
            "2. 使用 HTML + CSS + JavaScript\n" +
            "3. 代码要完整、可直接运行\n" +
            "4. 响应式设计，适配移动端\n" +
            "5. 现代化、专业的视觉效果\n" +
            "6. 不要使用 markdown 代码块标记，直接输出 HTML 代码\n" +
            "\n" +
            "重要：只返回 HTML 代码，不要有其他任何文字说明。";
        
        String userPrompt = "页面需求：" + description + "\n\n请只返回完整的 HTML 代码，使用 Tailwind CSS，不要有其他说明文字。";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 8192);
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", List.of(Map.of("type", "text", "text", userPrompt)));
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("system", systemPrompt);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", aiConfig.getApiKey());
        headers.set("anthropic-version", "2023-06-01");
        
        String url = aiConfig.getBaseUrl() + "/v1/messages";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("代码生成请求 URL: {}", url);
            log.info("代码生成请求 Body: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            log.info("代码生成响应: {}", response.getBody());
            
            String content = parseAnthropicResponse(response.getBody());
            if (content != null && !content.isEmpty()) {
                // 清理响应内容
                content = content.trim();
                if (content.startsWith("```html")) {
                    content = content.substring(7);
                } else if (content.startsWith("```")) {
                    content = content.substring(3);
                }
                if (content.endsWith("```")) {
                    content = content.substring(0, content.length() - 3);
                }
                content = content.trim();
                
                result.put("success", true);
                result.put("code", content);
                result.put("framework", framework);
                return result;
            } else {
                result.put("error", "无法解析 AI 响应内容为空");
                result.put("raw", response.getBody());
                return result;
            }
        } catch (Exception e) {
            log.error("AI 代码生成失败：", e);
            result.put("error", "AI代码生成失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 语音合成 TTS
     */
    public Map<String, Object> textToSpeech(TtsRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        requestBody.put("text", request.getText());
        requestBody.put("stream", false);
        
        Map<String, Object> voiceSetting = new HashMap<>();
        voiceSetting.put("voice_id", request.getVoiceId());
        voiceSetting.put("speed", request.getSpeed());
        voiceSetting.put("vol", 1);
        voiceSetting.put("pitch", 0);
        voiceSetting.put("emotion", "happy");
        requestBody.put("voice_setting", voiceSetting);
        
        Map<String, Object> audioSetting = new HashMap<>();
        audioSetting.put("sample_rate", 32000);
        audioSetting.put("bitrate", 128000);
        audioSetting.put("format", "mp3");
        audioSetting.put("channel", 1);
        requestBody.put("audio_setting", audioSetting);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());
        
        String url = "https://api.minimaxi.com/v1/t2a_v2";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("TTS 请求 URL: {}", url);
            log.info("TTS 请求 Body: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            String responseBody = response.getBody();
            log.info("TTS 响应: {}", responseBody);
            
            return parseTtsResponse(responseBody);
        } catch (Exception e) {
            log.error("TTS 调用失败：", e);
            result.put("error", "TTS调用失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 图像生成
     */
    public Map<String, Object> generateImage(ImageGenerateRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        requestBody.put("prompt", request.getPrompt());
        requestBody.put("aspect_ratio", request.getAspectRatio());
        requestBody.put("response_format", request.getResponseFormat());
        requestBody.put("n", request.getN());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());
        
        String url = "https://api.minimaxi.com/v1/image_generation";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("图像生成请求 URL: {}", url);
            log.info("图像生成请求 Body: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            String responseBody = response.getBody();
            log.info("图像生成响应: {}", responseBody);
            
            return parseImageResponse(responseBody);
        } catch (Exception e) {
            log.error("图像生成调用失败：", e);
            result.put("error", "图像生成失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 视频生成
     */
    public Map<String, Object> generateVideo(VideoGenerateRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", request.getModel());
        requestBody.put("prompt", request.getPrompt());
        requestBody.put("duration", request.getDuration());
        requestBody.put("resolution", request.getResolution());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());
        
        String url = "https://api.minimaxi.com/v1/video_generation";
        
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("视频生成请求 URL: {}", url);
            log.info("视频生成请求 Body: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            String responseBody = response.getBody();
            log.info("视频生成响应: {}", responseBody);
            
            return parseVideoResponse(responseBody);
        } catch (Exception e) {
            log.error("视频生成调用失败：", e);
            result.put("error", "视频生成失败: " + e.getMessage());
            return result;
        }
    }
    
    private String parseAnthropicResponse(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        
        try {
            Map<?, ?> json = objectMapper.readValue(responseBody, Map.class);
            
            if (json.containsKey("content")) {
                List<?> content = (List<?>) json.get("content");
                if (content != null && !content.isEmpty()) {
                    for (Object block : content) {
                        if (block instanceof Map) {
                            Map<?, ?> blockMap = (Map<?, ?>) block;
                            if ("text".equals(blockMap.get("type"))) {
                                return (String) blockMap.get("text");
                            }
                        }
                    }
                }
            }
            
            if (json.containsKey("type") && "error".equals(json.get("type"))) {
                log.error("API 错误: {}", json.get("error"));
            }
            
        } catch (JsonProcessingException e) {
            log.error("JSON 解析失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    private Map<String, Object> parseJsonResponse(String json) {
        Map<String, Object> result = new HashMap<>();
        try {
            json = json.trim();
            
            // 移除 markdown 代码块标记
            if (json.startsWith("```json")) {
                json = json.substring(7);
            } else if (json.startsWith("```")) {
                json = json.substring(3);
            }
            if (json.endsWith("```")) {
                json = json.substring(0, json.length() - 3);
            }
            json = json.trim();
            
            // 尝试直接解析
            try {
                Map<?, ?> parsed = objectMapper.readValue(json, Map.class);
                result.put("success", true);
                result.put("data", parsed);
                return result;
            } catch (Exception e) {
                // JSON 不完整，尝试修复
                log.warn("JSON 解析失败，尝试修复: {}", e.getMessage());
                String fixedJson = fixIncompleteJson(json);
                if (fixedJson != null) {
                    try {
                        Map<?, ?> parsed = objectMapper.readValue(fixedJson, Map.class);
                        result.put("success", true);
                        result.put("data", parsed);
                        result.put("fixed", true); // 标记为修复后的
                        return result;
                    } catch (Exception e2) {
                        log.error("修复后仍然解析失败: {}", e2.getMessage());
                    }
                }
            }
            
            result.put("error", "JSON解析失败");
            result.put("raw", json);
            return result;
        } catch (Exception e) {
            result.put("error", "JSON解析异常: " + e.getMessage());
            result.put("raw", json);
            return result;
        }
    }
    
    /**
     * 尝试修复不完整的 JSON
     */
    private String fixIncompleteJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        try {
            // 移除末尾的不完整内容（从最后一个完整的}或]往前找）
            int lastComplete = findLastCompleteBracket(json);
            if (lastComplete > 0) {
                json = json.substring(0, lastComplete + 1);
            }
            
            // 补全缺失的括号
            int openBraces = countChar(json, '{');
            int closeBraces = countChar(json, '}');
            int openBrackets = countChar(json, '[');
            int closeBrackets = countChar(json, ']');
            
            // 补全大括号
            while (closeBraces > openBraces) {
                json = "{" + json;
                openBraces++;
            }
            while (openBraces > closeBraces) {
                json = json + "}";
                closeBraces++;
            }
            
            // 补全中括号
            while (closeBrackets > openBrackets) {
                json = "[" + json;
                openBrackets++;
            }
            while (openBrackets > closeBrackets) {
                json = json + "]";
                closeBrackets++;
            }
            
            return json;
        } catch (Exception e) {
            log.error("修复 JSON 失败: {}", e.getMessage());
            return null;
        }
    }
    
    private int findLastCompleteBracket(String json) {
        int depth = 0;
        boolean inString = false;
        char prevChar = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && prevChar != '\\') {
                inString = !inString;
            } else if (!inString) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }
            }
            
            prevChar = c;
            
            // 当深度回到0且是结束符时，返回这个位置
            if (depth == 0 && (c == '}' || c == ']')) {
                // 检查后面是否还有未完成的逗号或属性
                String rest = json.substring(i + 1).trim();
                if (rest.isEmpty() || rest.equals(",") || rest.startsWith(",\n")) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private int countChar(String str, char c) {
        int count = 0;
        boolean inString = false;
        char prevChar = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '"' && prevChar != '\\') {
                inString = !inString;
            } else if (!inString && ch == c) {
                count++;
            }
            prevChar = ch;
        }
        return count;
    }
    
    private Map<String, Object> parseTtsResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<?, ?> json = objectMapper.readValue(responseBody, Map.class);
            
            Map<?, ?> data = (Map<?, ?>) json.get("data");
            if (data != null) {
                String audio = (String) data.get("audio");
                if (audio != null) {
                    result.put("success", true);
                    result.put("audio", audio);
                    result.put("format", "mp3");
                    return result;
                }
            }
            
            Map<?, ?> baseResp = (Map<?, ?>) json.get("base_resp");
            if (baseResp != null) {
                Object statusCode = baseResp.get("status_code");
                if (statusCode != null && !"0".equals(statusCode.toString())) {
                    result.put("error", "TTS API 错误: " + baseResp.get("status_msg"));
                    return result;
                }
            }
            
            result.put("error", "无法解析 TTS 响应");
            result.put("raw", responseBody);
            return result;
        } catch (Exception e) {
            result.put("error", "TTS 响应解析失败: " + e.getMessage());
            return result;
        }
    }
    
    private Map<String, Object> parseImageResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<?, ?> json = objectMapper.readValue(responseBody, Map.class);
            
            Map<?, ?> data = (Map<?, ?>) json.get("data");
            if (data != null) {
                List<?> imageUrls = (List<?>) data.get("image_urls");
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    result.put("success", true);
                    result.put("images", imageUrls);
                    return result;
                }
                List<?> imageBase64 = (List<?>) data.get("image_base64");
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    result.put("success", true);
                    result.put("images", imageBase64);
                    return result;
                }
            }
            
            Object id = json.get("id");
            if (id != null) {
                result.put("success", true);
                result.put("task_id", id);
                result.put("status", "processing");
                return result;
            }
            
            Map<?, ?> baseResp = (Map<?, ?>) json.get("base_resp");
            if (baseResp != null) {
                Object statusCode = baseResp.get("status_code");
                if (statusCode != null && !"0".equals(statusCode.toString())) {
                    result.put("error", "图像生成 API 错误: " + baseResp.get("status_msg"));
                    return result;
                }
            }
            
            result.put("error", "无法解析图像生成响应");
            result.put("raw", responseBody);
            return result;
        } catch (Exception e) {
            result.put("error", "图像生成响应解析失败: " + e.getMessage());
            return result;
        }
    }
    
    private Map<String, Object> parseVideoResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<?, ?> json = objectMapper.readValue(responseBody, Map.class);
            
            Object taskId = json.get("task_id");
            if (taskId != null) {
                result.put("success", true);
                result.put("task_id", taskId);
                result.put("status", "processing");
                return result;
            }
            
            Map<?, ?> baseResp = (Map<?, ?>) json.get("base_resp");
            if (baseResp != null) {
                Object statusCode = baseResp.get("status_code");
                if (statusCode != null && !"0".equals(statusCode.toString())) {
                    result.put("error", "视频生成 API 错误: " + baseResp.get("status_msg"));
                    return result;
                }
            }
            
            result.put("error", "无法解析视频生成响应");
            result.put("raw", responseBody);
            return result;
        } catch (Exception e) {
            result.put("error", "视频生成响应解析失败: " + e.getMessage());
            return result;
        }
    }
}
