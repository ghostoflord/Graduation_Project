package com.vn.capstone.controller;

import com.vn.capstone.domain.ManualChat;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.service.ManualChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ManualChatController {

    private final ManualChatService manualChatService;

    public ManualChatController(ManualChatService manualChatService) {
        this.manualChatService = manualChatService;
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
}
