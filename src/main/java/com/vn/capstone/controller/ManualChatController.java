package com.vn.capstone.controller;

import com.vn.capstone.domain.ManualChat;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.ChatBotService;
import com.vn.capstone.service.ManualChatService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ManualChatController {

    private final ManualChatService manualChatService;
    private final ChatBotService chatBotService;

    public ManualChatController(ManualChatService manualChatService, ChatBotService chatBotService) {
        this.manualChatService = manualChatService;
        this.chatBotService = chatBotService;
    }

    @PostMapping("/manual-chats")
    public ResponseEntity<RestResponse<ManualChat>> createManualChat(@RequestBody ManualChat manualChat) {
        ManualChat savedChat = manualChatService.save(manualChat);

        RestResponse<ManualChat> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setError(null);
        response.setMessage("Yêu cầu đã được gửi thành công");
        response.setData(savedChat);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/manual-chats/manual-chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = chatBotService.handleUserMessage(message);
        return ResponseEntity.ok(reply);
    }
}
