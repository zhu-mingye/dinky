/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.service.openai;

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

import lombok.RequiredArgsConstructor;

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
        headers.set(
                "Authorization", "Bearer " + systemConfiguration.getOpenAiKey().getValue());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("prompt", suggestionDTO.getSqlStatement());
        params.add("model", systemConfiguration.getOpenaiModelType().getValue());
        params.add(
                "max_tokens",
                String.valueOf(systemConfiguration.getOpenaiMaxTokens().getValue()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    systemConfiguration.getOpenAIBaseUrl().getValue(), request, String.class);
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
