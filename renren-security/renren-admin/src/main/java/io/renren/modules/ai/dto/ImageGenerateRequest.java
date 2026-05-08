package io.renren.modules.ai.dto;

import lombok.Data;

@Data
public class ImageGenerateRequest {
    private String prompt;
    private String model = "image-01";
    private String aspectRatio = "1:1";
    private String responseFormat = "url";
    private Integer n = 1;
}
