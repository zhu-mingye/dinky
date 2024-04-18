package org.dinky.service.openai;

import lombok.RequiredArgsConstructor;
import org.dinky.data.dto.SuggestionDTO;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.SystemConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class VendorAAdapter implements OpenAISuggestionService {


    /**
     * get suggestions for editor
     *
     * @param suggestionDTO
     * @return suggestions list
     */
    @Override
    public String getSuggestions(SuggestionDTO suggestionDTO) {
        SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
        if (!systemConfiguration.getEnableOpenAI().getValue()) {
           return "OpenAI is not enabled,if you want to use OpenAI,please enable it in the system configuration.";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + systemConfiguration.getOpenAiKey().getValue());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("prompt", suggestionDTO.getSqlStatement());
        params.add("model", systemConfiguration.getOpenaiModelType().getValue());
        params.add("max_tokens", String.valueOf(systemConfiguration.getOpenaiMaxTokens().getValue()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

         RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(systemConfiguration.getOpenAIBaseUrl().getValue(), request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new BusException("Vendor A API returned non-success status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new BusException("Error calling Vendor A API: " + e.getMessage());
        }
    }
}
