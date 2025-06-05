package com.vn.capstone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.vn.capstone.domain.ChatMessage;
import com.vn.capstone.domain.FaqRule;
import com.vn.capstone.repository.ChatMessageRepository;
import com.vn.capstone.repository.FaqRuleRepository;

@Service
public class ChatBotService {
    private final FaqRuleRepository faqRepo;
    private final ChatMessageRepository chatRepo;

    public ChatBotService(FaqRuleRepository faqRepo, ChatMessageRepository chatRepo) {
        this.faqRepo = faqRepo;
        this.chatRepo = chatRepo;
    }

    public String handleUserMessage(String userInput) {
        chatRepo.save(new ChatMessage("user", userInput, Instant.now()));

        Optional<FaqRule> matched = faqRepo.findAll()
                .stream()
                .filter(rule -> userInput.toLowerCase().contains(rule.getKeyword()))
                .findFirst();

        String botReply = matched.map(FaqRule::getAnswer).orElse("Xin lỗi, tôi chưa hiểu yêu cầu của bạn.");
        chatRepo.save(new ChatMessage("bot", botReply, Instant.now()));

        return botReply;
    }
}
