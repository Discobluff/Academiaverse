package com.academiaverse.service;

import com.academiaverse.entity.Chat;
import com.academiaverse.repository.ChatRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    @Transactional
    public Chat createChat(Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    public List<Chat> getChatsByUser(Integer userId) {
        return chatRepository.findByUser(userId);
    }
}
