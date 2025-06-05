package com.vn.capstone.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.capstone.service.ChatBotService;

@RestController
@RequestMapping("/api/v1/manual-chat")
public class ManualChatController {
    private final ChatBotService chatBotService;

    public ManualChatController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = chatBotService.handleUserMessage(message);
        return ResponseEntity.ok(reply);
    }
}
