package org.dinky.service.openai;

import lombok.Data;

@Data
public class OpenAISuggestionConfig {

    private String apiKey;
    private String model;
    private final String baseUrl = "https://vendor-a-api.com/openai";


}
