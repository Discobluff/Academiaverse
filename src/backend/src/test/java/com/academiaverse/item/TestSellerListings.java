package com.academiaverse.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
class TestSellerListings {

    private static final String mail = "test@example.com";

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
        if (userRepository.findByEmail(mail).isEmpty()) {
            User user = new User();
            user.setEmail(mail);
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            user.setFirstname("Test");
            user.setLastname("User");
            userRepository.save(user);
        }
    }

    Item createItem() throws Exception {
        Item item = new Item();
        item.setTitle("Test Listing");
        item.setDescription("Test Description");
        item.setPrice(10.0);
        item.setType("sale");
        item.setNotes("Test notes");
        item.setPaymentMethod("points");
        
        String json = objectMapper.writeValueAsString(item);
        assertDoesNotThrow(() -> mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)));
        return item;
    }

    @Test
    @WithMockUser(username = mail)
    void getSellerListingsShouldReturnOk() throws Exception {
        createItem();
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/items/seller_listings"))
                .andExpect(status().isOk()));
    }

    @Test
    @WithMockUser(username = mail)
    void getSellerListings() throws Exception {
        Item item = createItem();
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/items/seller_listings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value(item.getTitle()))
            .andExpect(jsonPath("$[0].description").value(item.getDescription()))
            .andExpect(jsonPath("$[0].price").value(item.getPrice()))
            .andExpect(jsonPath("$[0].type").value(item.getType()))
            .andExpect(jsonPath("$[0].notes").value(item.getNotes()))
            .andExpect(jsonPath("$[0].paymentMethod").value(item.getPaymentMethod()))
        );;
    }
}
