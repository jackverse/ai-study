package io.renren.modules.saas.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.utils.Result;
import io.renren.modules.ai.service.AiService;
import io.renren.modules.saas.config.TenantContext;
import io.renren.modules.saas.entity.SiteEntity;
import io.renren.modules.saas.entity.SitePageEntity;
import io.renren.modules.saas.service.SaaSAiChatService;
import io.renren.modules.saas.service.SaaSPageService;
import io.renren.modules.saas.service.SaaSSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SaaS AI建站助手控制器
 * 
 * 合并来源:
 * - renren-ai: io.renren.modules.ai.controller.AiController (AI服务能力)
 * - renren-saas-builder: com.openclaw.saas.controller.ai.AiChatController (自然语言建站对话)
 */
@Slf4j
@RestController
@RequestMapping("/saas/ai")
@RequiredArgsConstructor
@Tag(name = "SaaS AI建站助手", description = "通过自然语言描述建站，AI自动完成")
public class SaaSAiController {

    private final AiService aiService;
    private final SaaSAiChatService aiChatService;
    private final SaaSSiteService siteService;
    private final SaaSPageService pageService;

    /**
     * AI对话建站 (自然语言)
     * 
     * POST /saas/ai/chat
     * {
     *   "message": "帮我创建一个企业官网，包含首页、关于我们、产品中心、联系我们"
     * }
     */
    @PostMapping("/chat")
    @Operation(summary = "AI对话建站", description = "用自然语言描述建站需求，AI自动完成")
    public Result<String> chat(@RequestBody Map<String, String> request) {
        Long tenantId = TenantContext.getTenantId();
        Long userId = TenantContext.getUserId();
        String message = request.get("message");
        
        if (tenantId == null) tenantId = 1L;
        if (userId == null) userId = 1L;
        if (message == null || message.trim().isEmpty()) {
            return new Result<String>().error("消息内容不能为空");
        }
        
        log.info("🤖 AI建站请求 - tenantId: {}, userId: {}, message: {}", tenantId, userId, message);
        
        try {
            // 使用真实Spring AI服务进行对话
            String reply = aiChatService.chat(tenantId, userId, message);
            return new Result<String>().ok(reply);
        } catch (Exception e) {
            log.error("❌ AI对话失败", e);
            return new Result<String>().error("AI处理失败: " + e.getMessage());
        }
    }

    /**
     * AI生成页面JSON
     * 
     * POST /saas/ai/generate-page
     * {
     *   "description": "创建一个关于我们页面，包含公司介绍、团队成员、发展历程",
     *   "siteId": 1,
     *   "pageName": "about"
     * }
     */
    @PostMapping("/generate-page")
    @Operation(summary = "AI生成页面结构", description = "根据描述生成页面组件JSON")
    public Result<Map<String, Object>> generatePage(@RequestBody Map<String, Object> request) {
        Long tenantId = TenantContext.getTenantId();
        String description = (String) request.get("description");
        Long siteId = request.get("siteId") != null ? ((Number) request.get("siteId")).longValue() : null;
        String pageName = (String) request.get("pageName");
        
        try {
            Map<String, Object> pageSchema = aiService.generatePage(description);
            
            // 如果提供了siteId和pageName，自动创建页面
            if (siteId != null && pageName != null) {
                SitePageEntity page = pageService.generatePageByAI(
                    siteId, pageName, description, 
                    "/" + pageName, 
                    pageSchema.get("components") != null ? pageSchema.toString() : null,
                    pageSchema.toString()
                );
                pageSchema.put("pageId", page.getId());
            }
            
            return new Result<Map<String, Object>>().ok(pageSchema);
        } catch (Exception e) {
            log.error("❌ AI页面生成失败", e);
            return new Result<Map<String, Object>>().error("AI页面生成失败: " + e.getMessage());
        }
    }

    /**
     * AI生成完整代码 (HTML + Tailwind CSS)
     * 
     * POST /saas/ai/generate-code
     * {
     *   "description": "创建一个企业首页，包含Banner、产品展示、Footer",
     *   "framework": "tailwind"
     * }
     */
    @PostMapping("/generate-code")
    @Operation(summary = "AI生成页面代码", description = "生成完整的HTML页面代码")
    public Result<Map<String, Object>> generateCode(@RequestBody Map<String, Object> request) {
        Long tenantId = TenantContext.getTenantId();
        String description = (String) request.get("description");
        String framework = (String) request.getOrDefault("framework", "tailwind");
        
        try {
            String code = aiService.generateCode(description, framework);
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", code);
            result.put("framework", framework);
            
            return new Result<Map<String, Object>>().ok(result);
        } catch (Exception e) {
            log.error("❌ AI代码生成失败", e);
            return new Result<Map<String, Object>>().error("AI代码生成失败: " + e.getMessage());
        }
    }

    /**
     * 批量生成页面
     */
    @PostMapping("/generate-pages")
    @Operation(summary = "批量生成页面", description = "根据描述批量创建多个页面")
    public Result<String> generatePages(@RequestBody Map<String, Object> request) {
        return new Result<String>().ok("批量生成功能待实现，请先通过 /saas/ai/chat 对话创建站点和页面");
    }

    /**
     * AI创建完整站点
     * 
     * POST /saas/ai/create-site
     * {
     *   "name": "我的企业官网",
     *   "description": "企业官网，包含首页、关于我们、产品中心、联系我们",
     *   "category": "enterprise"
     * }
     */
    @PostMapping("/create-site")
    @Operation(summary = "AI创建完整站点", description = "根据描述创建完整站点和默认页面")
    public Result<SiteEntity> createSite(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        String category = request.get("category");
        
        try {
            // 1. 创建站点
            SiteEntity site = siteService.createSite(name, description, category);
            
            // 2. 调用AI生成默认页面结构
            Map<String, Object> homeSchema = aiService.generatePage("网站首页，包含Banner和公司简介");
            
            // 3. 创建首页
            pageService.generatePageByAI(
                site.getId(), "首页", "网站首页",
                "/", homeSchema.toString(), homeSchema.toString()
            );
            
            return new Result<SiteEntity>().ok(site);
        } catch (Exception e) {
            log.error("❌ AI站点创建失败", e);
            return new Result<SiteEntity>().error("AI站点创建失败: " + e.getMessage());
        }
    }
}
