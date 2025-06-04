package com.vn.capstone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.vn.capstone.domain.request.ChatGPTRequest;
import com.vn.capstone.domain.request.PromptRequest;
import com.vn.capstone.domain.response.chat.ChatGPTResponse;

@Service
public class ChatService {
    private final RestClient restClient;

    public ChatService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.model}")
    private String model;

    public String getChatResponse(PromptRequest promptRequest) {
        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(
                model,
                List.of(new ChatGPTRequest.Message("user", promptRequest.prompt())));

        ChatGPTResponse response = restClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(chatGPTRequest)
                .retrieve()
                .body(ChatGPTResponse.class);
        return response.choices().get(0).message().content();
    }
}
