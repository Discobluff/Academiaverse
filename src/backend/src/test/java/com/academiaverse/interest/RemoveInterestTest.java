package com.academiaverse.interest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class RemoveInterestTest {

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
        // Create buyer
        if (userRepository.findByEmail("buyerremove@example.com").isEmpty()) {
            User buyer = new User();
            buyer.setEmail("buyerremove@example.com");
            buyer.setPassword(passwordEncoder.encode("password"));
            buyer.setRole("ROLE_USER");
            buyer.setFirstname("BuyerRemove");
            buyer.setLastname("Test");
            userRepository.save(buyer);
        }

        // Create seller
        final User seller = userRepository.findByEmail("sellerremove@example.com").orElseGet(() -> {
            User s = new User();
            s.setEmail("sellerremove@example.com");
            s.setPassword(passwordEncoder.encode("password"));
            s.setRole("ROLE_USER");
            s.setFirstname("SellerRemove");
            s.setLastname("Test");
            return userRepository.save(s);
        });

        item = new Item();
        item.setTitle("Item to be removed");
        item.setDescription("Test");
        item.setPrice(10.0);
        item.setType("sale");
        item.setPaymentMethod("cash");
        item.setOwner(seller);
        item = itemRepository.save(item);
    }

    @Test
    @WithMockUser(username = "buyerremove@example.com")
    void shouldRemoveInterest() throws Exception {
        // First create an interest
        Map<String, Long> body = Map.of("itemId", item.getId());
        mockMvc.perform(post("/api/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // Now remove it
        assertDoesNotThrow(() -> mockMvc.perform(delete("/api/interests/" + item.getId()))
                .andExpect(status().isOk()));

        // Verify the user now has zero interests
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/interests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)));
    }
}