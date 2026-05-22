package com.academiaverse.chat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.TestHelper;
import com.academiaverse.dto.request.ChatRequest;
import com.academiaverse.entity.Item;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestHelper.class)   
class TestChat {

    @Autowired
    private TestHelper testHelper;
    
    private String mailSeller = "seller@example.com";
    private String mailBuyer = "buyer@example.com";

    private static final String BUYER_EMAIL = "buyer@example.com";

    @Autowired
    private ObjectMapper objectMapper;


    void sendMessage(String mail, ChatRequest chatRequest, ResultMatcher status) throws Exception {
        String json = objectMapper.writeValueAsString(chatRequest);
        testHelper.testPost(mail, "/api/chat", MediaType.APPLICATION_JSON, json, status);
    }

    @Test
    void testSendMessage() throws Exception {
        Item item = testHelper.createBaseItem(mailSeller);
        ChatRequest chat = new ChatRequest();
        chat.setMessage("Hello, I'm interested in your item.");
        chat.setItemId(item.getId());
        chat.setBuyerId(testHelper.getUser(mailBuyer).getId());
        assertDoesNotThrow(() -> sendMessage(mailBuyer, chat, status().isOk()));
    }
    
    @Test
    @WithMockUser(username = BUYER_EMAIL)
    void testGetMessageBuyer() throws Exception {
        Item item = testHelper.createBaseItem(mailSeller);
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessage("Hello, I'm interested in your item.");
        chatRequest.setItemId(item.getId());
        chatRequest.setBuyerId(testHelper.getUser(mailBuyer).getId());
        sendMessage(mailBuyer, chatRequest, status().isOk());
        testHelper.testGet(mailBuyer, "/api/chat", List.of(), status().isOk(), jsonPath("$[0].message").value("Hello, I'm interested in your item."));
    }
}
