package io.renren.modules.ai.controller;

import io.renren.common.utils.Result;
import io.renren.modules.ai.dto.ChatRequest;
import io.renren.modules.ai.dto.PageGenerateRequest;
import io.renren.modules.ai.dto.TtsRequest;
import io.renren.modules.ai.dto.ImageGenerateRequest;
import io.renren.modules.ai.dto.VideoGenerateRequest;
import io.renren.modules.ai.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody ChatRequest request) {
        String reply = aiService.chat(request.getMessage());
        return new Result<String>().ok(reply);
    }

    @PostMapping("/page/generate")
    public Result<Map<String, Object>> generatePage(@RequestBody PageGenerateRequest request) {
        Map<String, Object> pageSchema = aiService.generatePage(request.getDescription());
        return new Result<Map<String, Object>>().ok(pageSchema);
    }

    @PostMapping("/code/generate")
    public Result<Map<String, Object>> generateCode(@RequestBody Map<String, Object> request) {
        String description = (String) request.get("description");
        String framework = (String) request.getOrDefault("framework", "tailwind");
        Map<String, Object> result = aiService.generateCode(description, framework);
        return new Result<Map<String, Object>>().ok(result);
    }

    @PostMapping("/tts")
    public Result<Map<String, Object>> textToSpeech(@RequestBody TtsRequest request) {
        Map<String, Object> result = aiService.textToSpeech(request);
        return new Result<Map<String, Object>>().ok(result);
    }

    @PostMapping("/image/generate")
    public Result<Map<String, Object>> generateImage(@RequestBody ImageGenerateRequest request) {
        Map<String, Object> result = aiService.generateImage(request);
        return new Result<Map<String, Object>>().ok(result);
    }

    @PostMapping("/video/generate")
    public Result<Map<String, Object>> generateVideo(@RequestBody VideoGenerateRequest request) {
        Map<String, Object> result = aiService.generateVideo(request);
        return new Result<Map<String, Object>>().ok(result);
    }
}
