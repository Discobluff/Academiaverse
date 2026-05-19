package com.academiaverse.item;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.dto.request.RatingRequest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ItemService;
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
class TestRatings {

    private static final String mailSeller = "sellerRatings@example.com";
    private static final String mailBuyer = "buyerRatings@example.com";

    @Autowired
    private ItemService itemService;

    private User seller;
    private User buyer;
    private Item item;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {

        if (userRepository.findByEmail(mailSeller).isEmpty()) {
            User user = new User();
            user.setEmail(mailSeller);
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            seller = userRepository.save(user);
        }
        else {
            seller = userRepository.findByEmail(mailSeller).get();
        }
        if (userRepository.findByEmail(mailBuyer).isEmpty()) {
            User user = new User();
            user.setEmail(mailBuyer);
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_USER");
            buyer = userRepository.save(user);
        }
        else {
            buyer = userRepository.findByEmail(mailBuyer).get();
        }
        item = createItem();
    }

    @WithMockUser(username = mailSeller)
    Item createItem() throws Exception {
        Item item = new Item();
        item.setTitle("Test Listing");
        item.setDescription("Test Description");
        item.setOwner(seller);
        item.setPrice(1.0);
        item.setType("sale");
        item.setNotes("Test notes");
        item.setPaymentMethod("points");

        return itemService.createItem(item, seller);
    }

    @Test
    @WithMockUser(username = mailBuyer)
    void rateItem() throws Exception {
        RatingRequest request = new RatingRequest(item.getId(), 5);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = mailBuyer)
    void getRatings() throws Exception {
        mockMvc.perform(get("/api/ratings")
                .param("itemId", String.valueOf(item.getId())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = mailBuyer)
    void getUserRating() throws Exception {
        mockMvc.perform(get("/api/ratings/user")
                .param("itemId", String.valueOf(item.getId())))
                .andExpect(status().isNotFound());
    }

}
