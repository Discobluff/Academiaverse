package com.academiaverse.interest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CreateInterestTest {

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

    private User buyer;
    private User seller;
    private Item item;

    @BeforeEach
    void setUp() {
        buyer = userRepository.findByEmail("buyer14@example.com").orElseGet(() -> {
            User b = new User();
            b.setEmail("buyer14@example.com");
            b.setPassword(passwordEncoder.encode("password"));
            b.setRole("ROLE_USER");
            b.setFirstname("Buyer14");
            b.setLastname("Test");
            return userRepository.save(b);
        });

        seller = userRepository.findByEmail("seller14@example.com").orElseGet(() -> {
            User s = new User();
            s.setEmail("seller14@example.com");
            s.setPassword(passwordEncoder.encode("password"));
            s.setRole("ROLE_USER");
            s.setFirstname("Seller14");
            s.setLastname("Test");
            return userRepository.save(s);
        });

        item = new Item();
        item.setTitle("Item for Interest Test");
        item.setDescription("A test item");
        item.setPrice(10.0);
        item.setType("sale");
        item.setPaymentMethod("cash");
        item.setOwner(seller);
        item = itemRepository.save(item);
    }

    @Test
    @WithMockUser(username = "buyer14@example.com")
    void shouldCreateInterestAndChangeItemStatus() throws Exception {
        Map<String, Long> body = Map.of("itemId", item.getId());

        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.status").value("Interest"))
                .andExpect(jsonPath("$.item.id").value(item.getId().intValue()));
    }
}