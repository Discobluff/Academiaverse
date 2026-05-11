package com.academiaverse.item;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
class ItemEditDeleteTest {

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

    private User seller;
    private User other;
    private Item item;

    @BeforeEach
    void setUp() {
        seller = userRepository.findByEmail("seller@example.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("seller@example.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            u.setFirstname("Seller");
            u.setLastname("Test");
            return userRepository.save(u);
        });

        other = userRepository.findByEmail("other@example.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("other@example.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            u.setFirstname("Other");
            u.setLastname("User");
            return userRepository.save(u);
        });

        item = new Item();
        item.setTitle("Original Title");
        item.setDescription("Original Desc");
        item.setPrice(10.0);
        item.setType("sale");
        item.setNotes("Original notes");
        item.setPaymentMethod("cash");
        item.setOwner(seller);
        item = itemRepository.save(item);
    }

    @Test
    @WithMockUser(username = "seller@example.com")
    void shouldUpdateItem() throws Exception {
        Item updated = new Item();
        updated.setTitle("Updated Title");
        updated.setDescription("Updated Desc");
        updated.setPrice(20.0);
        updated.setType("rent");
        updated.setNotes("Updated notes");
        updated.setPaymentMethod("points");

        mockMvc.perform(put("/api/items/" + item.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller@example.com")
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/items/" + item.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    void shouldNotDeleteIfNotOwner() throws Exception {
        // other is a valid user but not the owner -> should get 403
        mockMvc.perform(delete("/api/items/" + item.getId()))
                .andExpect(status().isForbidden());
    }
}
