package com.academiaverse.chat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.dto.request.ChatRequest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@AutoConfigureMockMvc
class TestChat {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;  

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Item item;
    private User buyer;
    private User seller;

    @BeforeEach
    void setUp() {
        buyer = userRepository.findByEmail("buyer@example.com").orElseGet(() -> {
            User newBuyer = new User();
            newBuyer.setEmail("buyer@example.com");
            newBuyer.setPassword(passwordEncoder.encode("password"));
            newBuyer.setRole("ROLE_USER");
            newBuyer.setFirstname("Buyer");
            newBuyer.setLastname("User");
            return userRepository.save(newBuyer);
        });

        seller = userRepository.findByEmail("seller@example.com").orElseGet(() -> {
            User newSeller = new User();
            newSeller.setEmail("seller@example.com");
            newSeller.setPassword(passwordEncoder.encode("password"));
            newSeller.setRole("ROLE_USER");
            newSeller.setFirstname("Seller");
            newSeller.setLastname("User");
            return userRepository.save(newSeller);
        });

        item = new Item();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setPrice(10.0);
        item.setType("sale");
        item.setOwner(seller);
        item = itemRepository.save(item);
    }

    void sendMessage(ChatRequest chatRequest, ResultMatcher status) throws Exception {
        String json = objectMapper.writeValueAsString(chatRequest);
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status);
    }

    @Test
    @WithMockUser(username = "buyer@example.com")
    void testSendMessage() throws Exception {
        ChatRequest chat = new ChatRequest();
        chat.setMessage("Hello, I'm interested in your item.");
        chat.setItemId(item.getId());
        chat.setBuyerId(buyer.getId());
        assertDoesNotThrow(() -> sendMessage(chat, status().isOk()));
    }

    @Test
    @WithMockUser(username = "buyer@example.com")
    void testGetMessageBuyer() throws Exception {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessage("Hello, I'm interested in your item.");
        chatRequest.setItemId(item.getId());
        chatRequest.setBuyerId(buyer.getId());
        assertDoesNotThrow(() -> sendMessage(chatRequest, status().isOk()));
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/chat"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].message").value("Hello, I'm interested in your item.")));
    }

}
