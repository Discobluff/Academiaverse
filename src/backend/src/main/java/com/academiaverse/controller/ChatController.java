package com.academiaverse.controller;

import com.academiaverse.dto.request.ChatRequest;
import com.academiaverse.entity.Chat;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ChatService;
import com.academiaverse.service.ItemService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Chat>> getMessages(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatService.getChatsByUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Chat> sendMessage(@RequestBody ChatRequest chatRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Chat chat = new Chat();
        chat.setMessage(chatRequest.getMessage());
        chat.setItem(itemRepository.findById(chatRequest.getItemId()).orElseThrow(() -> new RuntimeException("Item not found")));
        chat.setBuyer(userRepository.findById(chatRequest.getBuyerId()).orElseThrow(() -> new RuntimeException("Buyer not found")));
        chat.setSender(user);
        return ResponseEntity.ok(chatService.createChat(chat));
    }
}
