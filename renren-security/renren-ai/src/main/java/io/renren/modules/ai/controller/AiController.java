package io.renren.modules.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.common.utils.Result;
import io.renren.modules.ai.dto.ChatRequest;
import io.renren.modules.ai.dto.PageGenerateRequest;
import io.renren.modules.ai.entity.AiGenerationHistoryEntity;
import io.renren.modules.ai.service.AiService;
import io.renren.modules.ai.service.TenantService;
import io.renren.modules.ai.dao.AiGenerationHistoryDao;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 建站控制器
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;
    private final TenantService tenantService;
    private final AiGenerationHistoryDao aiHistoryDao;
    private final ObjectMapper objectMapper;

    public AiController(AiService aiService, TenantService tenantService, 
                       AiGenerationHistoryDao aiHistoryDao, ObjectMapper objectMapper) {
        this.aiService = aiService;
        this.tenantService = tenantService;
        this.aiHistoryDao = aiHistoryDao;
        this.objectMapper = objectMapper;
    }

    /**
     * 通用对话接口
     */
    @PostMapping("/chat")
    public Result<String> chat(
            @RequestBody ChatRequest request,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        try {
            String reply = aiService.chat(request.getMessage());
            
            // 记录使用量
            if (tenantId != null) {
                tenantService.incrementAiUsed(tenantId);
            }
            
            return new Result<String>().ok(reply);
        } catch (Exception e) {
            return new Result<String>().error("AI对话失败: " + e.getMessage());
        }
    }

    /**
     * AI 生成页面
     */
    @PostMapping("/page/generate")
    public Result<Map<String, Object>> generatePage(
            @RequestBody PageGenerateRequest request,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestParam(value = "siteId", required = false) Long siteId,
            @RequestParam(value = "pageId", required = false) Long pageId) {
        try {
            Map<String, Object> pageSchema = aiService.generatePage(request.getDescription());
            
            // 记录使用量
            if (tenantId != null) {
                tenantService.incrementAiUsed(tenantId);
                
                // 保存生成历史
                AiGenerationHistoryEntity history = new AiGenerationHistoryEntity();
                history.setTenantId(tenantId);
                history.setSiteId(siteId);
                history.setPageId(pageId);
                history.setPrompt(request.getDescription());
                history.setGeneratedConfig(objectMapper.writeValueAsString(pageSchema));
                history.setStatus(1);
                history.setCreatedAt(new java.util.Date());
                aiHistoryDao.insert(history);
            }
            
            return new Result<Map<String, Object>>().ok(pageSchema);
        } catch (Exception e) {
            // 保存失败记录
            if (tenantId != null) {
                try {
                    AiGenerationHistoryEntity history = new AiGenerationHistoryEntity();
                    history.setTenantId(tenantId);
                    history.setSiteId(siteId);
                    history.setPageId(pageId);
                    history.setPrompt(request.getDescription());
                    history.setErrorMsg(e.getMessage());
                    history.setStatus(0);
                    history.setCreatedAt(new java.util.Date());
                    aiHistoryDao.insert(history);
                } catch (Exception ex) {
                    // ignore
                }
            }
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "AI页面生成失败: " + e.getMessage());
            return new Result<Map<String, Object>>().error("生成失败");
        }
    }

    /**
     * AI 代码生成
     */
    @PostMapping("/code/generate")
    public Result<Map<String, Object>> generateCode(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        try {
            String description = (String) request.get("description");
            String framework = (String) request.getOrDefault("framework", "tailwind");
            
            String code = aiService.generateCode(description, framework);
            
            // 记录使用量
            if (tenantId != null) {
                tenantService.incrementAiUsed(tenantId);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", code);
            result.put("framework", framework);
            
            return new Result<Map<String, Object>>().ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "AI代码生成失败: " + e.getMessage());
            return new Result<Map<String, Object>>().error("生成失败");
        }
    }

    /**
     * 获取 AI 生成历史
     */
    @GetMapping("/history")
    public Result<List<AiGenerationHistoryEntity>> getHistory(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        int offset = (page - 1) * limit;
        List<AiGenerationHistoryEntity> history = aiHistoryDao.selectByTenantId(tenantId, offset, limit);
        return new Result<List<AiGenerationHistoryEntity>>().ok(history);
    }
}
