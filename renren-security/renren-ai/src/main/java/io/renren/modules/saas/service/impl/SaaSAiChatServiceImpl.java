package io.renren.modules.saas.service.impl;

import io.renren.modules.ai.service.AiService;
import io.renren.modules.saas.entity.SiteEntity;
import io.renren.modules.saas.entity.SitePageEntity;
import io.renren.modules.saas.service.SaaSAiChatService;
import io.renren.modules.saas.service.SaaSPageService;
import io.renren.modules.saas.service.SaaSSiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * SaaS AI对话服务实现
 * 
 * 合并了:
 * - renren-saas-builder: AiChatController 的模拟对话逻辑
 * - renren-ai: AiService 的真实Spring AI集成
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaaSAiChatServiceImpl implements SaaSAiChatService {

    private final AiService aiService;
    private final SaaSSiteService siteService;
    private final SaaSPageService pageService;

    @Override
    @Transactional
    public String chat(Long tenantId, Long userId, String message) {
        log.info("🤖 AI建站对话 - tenantId: {}, userId: {}, message: {}", tenantId, userId, message);
        
        try {
            // 优先使用真实Spring AI服务
            String aiReply = aiService.chat(message);
            
            // 根据消息内容自动执行建站操作
            if (message.contains("创建") && (message.contains("官网") || message.contains("网站") || message.contains("站点"))) {
                return buildSiteFromChat(aiReply, message, tenantId);
            }
            
            return aiReply;
        } catch (Exception e) {
            // 如果Spring AI调用失败，返回友好的引导消息
            log.warn("⚠️ Spring AI调用失败，使用模拟响应: {}", e.getMessage());
            return simulateAIResponse(message);
        }
    }

    /**
     * 根据对话内容构建站点
     */
    private String buildSiteFromChat(String aiReply, String message, Long tenantId) {
        String siteName = extractSiteName(message);
        String siteDescription = message;
        
        try {
            // 创建站点
            SiteEntity site = siteService.createSite(siteName, siteDescription, detectCategory(message));
            
            // 根据关键词创建默认页面
            if (message.contains("首页") || message.contains("官网")) {
                Map<String, Object> homeSchema = aiService.generatePage("网站首页，包含Banner和公司简介");
                pageService.generatePageByAI(site.getId(), "首页", "网站首页", "/", 
                    homeSchema.toString(), homeSchema.toString());
            }
            if (message.contains("关于我们") || message.contains("关于")) {
                Map<String, Object> aboutSchema = aiService.generatePage("关于我们页面，包含公司介绍、团队成员");
                pageService.generatePageByAI(site.getId(), "关于我们", "关于我们", "/about", 
                    aboutSchema.toString(), aboutSchema.toString());
            }
            if (message.contains("产品中心") || message.contains("产品")) {
                Map<String, Object> productSchema = aiService.generatePage("产品展示页面，包含产品分类和产品列表");
                pageService.generatePageByAI(site.getId(), "产品中心", "产品中心", "/products", 
                    productSchema.toString(), productSchema.toString());
            }
            if (message.contains("联系") || message.contains("联系我们")) {
                Map<String, Object> contactSchema = aiService.generatePage("联系我们页面，包含联系表单和地图");
                pageService.generatePageByAI(site.getId(), "联系我们", "联系我们", "/contact", 
                    contactSchema.toString(), contactSchema.toString());
            }
            
            return aiReply + "\n\n✅ 站点已自动创建！站点ID: " + site.getId() + "，名称: " + siteName;
        } catch (Exception e) {
            log.error("❌ 自动建站失败: {}", e.getMessage());
            return aiReply + "\n\n⚠️ 站点创建过程中出现问题，请稍后重试。";
        }
    }

    /**
     * 模拟AI响应（当Spring AI不可用时）
     */
    private String simulateAIResponse(String message) {
        if (message.contains("企业官网") || message.contains("官网")) {
            return """
                🤖 AI建站助手已收到你的请求！
                
                我可以帮你创建一个企业官网，包含以下标准页面：
                - 🏠 首页（公司Banner + 产品展示）
                - 📖 关于我们（公司介绍 + 团队成员）
                - 📦 产品中心（产品分类 + 详情）
                - 📞 联系我们（表单 + 地图）
                
                要开始创建吗？请告诉我：
                1. 你的公司/网站名称
                2. 所属行业（科技/医疗/教育/零售等）
                3. 需要的页面有哪些
                
                或者直接说"帮我创建"，我使用默认配置！
                """;
        } else if (message.contains("博客") || message.contains("blog")) {
            return """
                📝 博客站点创建中...
                
                我可以帮你创建一个个人/技术博客，包含：
                - 🏠 首页（最新文章列表）
                - 📚 文章分类
                - 👤 关于博主
                - 📬 留言反馈
                
                请告诉我你的博客主题和名称。
                """;
        } else {
            return """
                🤖 我明白了！
                
                作为AI建站助手，我可以帮你：
                - 🏢 创建企业官网
                - 📝 创建博客网站
                - 🛒 创建电商站点
                - 📄 创建落地页/活动页
                
                只需要告诉我你想要什么样的网站，我会帮你一站式搞定！
                
                示例：
                "帮我建一个科技公司官网"
                "创建一个卖衣服的电商网站"
                "做一个母亲节活动落地页"
                """;
        }
    }

    private String extractSiteName(String message) {
        // 简单的站点名称提取
        if (message.contains("的")) {
            int idx = message.indexOf("的");
            if (idx > 0 && idx < 20) {
                return message.substring(0, idx);
            }
        }
        if (message.contains("创建")) {
            int idx = message.indexOf("创建");
            if (idx > 0) {
                String sub = message.substring(0, idx);
                if (sub.length() > 2) {
                    return sub.length() > 20 ? sub.substring(sub.length() - 20) : sub;
                }
            }
        }
        return "我的网站";
    }

    private String detectCategory(String message) {
        if (message.contains("电商") || message.contains("商城") || message.contains("商店")) return "mall";
        if (message.contains("博客") || message.contains("个人")) return "blog";
        if (message.contains("落地页") || message.contains("活动")) return "landing";
        return "enterprise";
    }
}
