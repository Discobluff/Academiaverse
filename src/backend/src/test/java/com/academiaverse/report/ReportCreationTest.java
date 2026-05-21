package com.academiaverse.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ReportCreationTest {

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

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("reporter@example.com").isEmpty()) {
            User reporter = new User();
            reporter.setEmail("reporter@example.com");
            reporter.setPassword(passwordEncoder.encode("password"));
            reporter.setRole("ROLE_USER");
            reporter.setFirstname("Reporter");
            reporter.setLastname("User");
            userRepository.save(reporter);
        }

        final User owner = userRepository.findByEmail("owner@example.com").orElseGet(() -> {
            User o = new User();
            o.setEmail("owner@example.com");
            o.setPassword(passwordEncoder.encode("password"));
            o.setRole("ROLE_USER");
            o.setFirstname("Owner");
            o.setLastname("User");
            return userRepository.save(o);
        });

        item = new Item();
        item.setTitle("Reported Item");
        item.setDescription("Item to be reported");
        item.setPrice(10.0);
        item.setType("sale");
        item.setPaymentMethod("cash");
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    @WithMockUser(username = "reporter@example.com")
    void shouldCreateReport() throws Exception {
        Map<String, Object> body = Map.of(
                "itemId", item.getId(),
                "reason", "Inappropriate content"
        );

        assertDoesNotThrow(() -> mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk()));
    }
}