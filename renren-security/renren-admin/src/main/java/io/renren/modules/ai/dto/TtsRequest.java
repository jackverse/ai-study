package io.renren.modules.ai.dto;

import lombok.Data;

@Data
public class TtsRequest {
    private String text;
    private String model = "speech-2.8-hd";
    private String voiceId = "male-qn-qingse";
    private Float speed = 1.0f;
    private String stream = "false";
}
