package com.academiaverse.item;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemCreationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("test@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("test@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            user.setFirstname("Test");
            user.setLastname("User");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createItemShouldReturnOk() throws Exception {
        Item item = new Item();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setPrice(10.0);
        item.setType("sale");

        String json = objectMapper.writeValueAsString(item);
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
