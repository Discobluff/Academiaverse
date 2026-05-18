package com.academiaverse.points;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
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
class PointsPurchaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Item item;

    @BeforeEach
    void setUp() {
        // Create buyer with 20 points
        userRepository.findByEmail("buyerpoints@example.com").orElseGet(() -> {
            User b = new User();
            b.setEmail("buyerpoints@example.com");
            b.setPassword(passwordEncoder.encode("password"));
            b.setRole("ROLE_USER");
            b.setFirstname("Buyer");
            b.setLastname("Points");
            b.setPoints(20);
            return userRepository.save(b);
        });

        // Create seller with 5 points
        User seller = userRepository.findByEmail("sellerpoints@example.com").orElseGet(() -> {
            User s = new User();
            s.setEmail("sellerpoints@example.com");
            s.setPassword(passwordEncoder.encode("password"));
            s.setRole("ROLE_USER");
            s.setFirstname("Seller");
            s.setLastname("Points");
            s.setPoints(5);
            return userRepository.save(s);
        });

        // Create an item priced at 10 points, payment method "points", owned by seller
        item = itemRepository.findByOwner(seller).iterator().hasNext() ?
                itemRepository.findByOwner(seller).iterator().next() : null;
        if (item == null || !"points".equals(item.getPaymentMethod())) {
            item = new Item();
            item.setTitle("Points Item");
            item.setDescription("Buy with points");
            item.setPrice(10.0);
            item.setType("sale");
            item.setPaymentMethod("points");
            item.setOwner(seller);
            itemRepository.save(item);
        }
    }

    @Test
    @WithMockUser(username = "buyerpoints@example.com")
    void shouldBuyWithPoints() throws Exception {
        mockMvc.perform(post("/api/items/" + item.getId() + "/buy-with-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerPoints").value(10))   // 20 - 10
                .andExpect(jsonPath("$.sellerPoints").value(15)); // 5 + 10
    }
}