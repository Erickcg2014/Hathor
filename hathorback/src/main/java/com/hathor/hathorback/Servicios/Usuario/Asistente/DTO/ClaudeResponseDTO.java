package com.hathor.hathorback.Servicios.Usuario.Asistente.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ClaudeResponseDTO {

    private String id;
    private String type;
    private String role;
    private List<ContentBlock> content;
    private String model;

    @JsonProperty("stop_reason")     
    private String stopReason;

    @JsonProperty("stop_sequence")
    private String stopSequence;

    private Usage usage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentBlock {
        private String type;
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("input_tokens")
        private int inputTokens;

        @JsonProperty("output_tokens")
        private int outputTokens;
    }

    public String getTexto() {
        if (content == null || content.isEmpty()) return "";
        return content.stream()
            .filter(c -> "text".equals(c.getType()))
            .map(ContentBlock::getText)
            .findFirst()
            .orElse("");
    }
}