package io.renren.modules.ai.dto;

import lombok.Data;

@Data
public class VideoGenerateRequest {
    private String prompt;
    private String model = "MiniMax-Hailuo-2.3";
    private Integer duration = 6;
    private String resolution = "768P";
}
