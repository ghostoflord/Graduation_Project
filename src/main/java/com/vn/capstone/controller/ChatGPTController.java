package com.vn.capstone.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.domain.request.PromptRequest;
import com.vn.capstone.service.ChatService;

@RestController
@RequestMapping("/api/v1")
public class ChatGPTController {
    private final ChatService chatService;

    public ChatGPTController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody PromptRequest promptRequest) {
        return chatService.getChatResponse(promptRequest);
    }
}