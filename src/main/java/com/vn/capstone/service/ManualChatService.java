package com.vn.capstone.service;

import com.vn.capstone.domain.ManualChat;
import com.vn.capstone.repository.ManualChatRepository;
import org.springframework.stereotype.Service;

@Service
public class ManualChatService {

    private final ManualChatRepository manualChatRepository;

    public ManualChatService(ManualChatRepository manualChatRepository) {
        this.manualChatRepository = manualChatRepository;
    }

    public ManualChat save(ManualChat manualChat) {
        return manualChatRepository.save(manualChat);
    }
}
