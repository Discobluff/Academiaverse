package com.academiaverse.points;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PointsPurchaseTest {

    private static final String BUYER_EMAIL = "buyerpoints@example.com";
    private static final String PAYMENT_METHOD_POINTS = "points";

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
        User buyer = userRepository.findByEmail(BUYER_EMAIL).orElseGet(() -> {
            User b = new User();
            b.setEmail(BUYER_EMAIL);
            b.setPassword(passwordEncoder.encode("password"));
            b.setRole("ROLE_USER");
            b.setFirstname("Buyer");
            b.setLastname("Points");
            b.setPoints(20);
            return userRepository.save(b);
        });
        if (buyer.getPoints() == null || buyer.getPoints() != 20) {
            buyer.setPoints(20);
            userRepository.save(buyer);
        }

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
        if (seller.getPoints() == null || seller.getPoints() != 5) {
            seller.setPoints(5);
            userRepository.save(seller);
        }

        Item existing = null;
        for (Item i : itemRepository.findByOwner(seller)) {
            if (PAYMENT_METHOD_POINTS.equals(i.getPaymentMethod())) {
                existing = i;
                break;
            }
        }
        if (existing != null) {
            item = existing;
        } else {
            item = new Item();
            item.setTitle("Points Item");
            item.setDescription("Buy with points");
            item.setPrice(10.0);
            item.setType("sale");
            item.setPaymentMethod(PAYMENT_METHOD_POINTS);
            item.setOwner(seller);
            item = itemRepository.save(item);
        }
    }

    @Test
    @WithMockUser(username = BUYER_EMAIL)
    void shouldBuyWithPointsReturnsOk() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(post("/api/items/" + item.getId() + "/buy-with-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()));
    }

    @Test
    @WithMockUser(username = BUYER_EMAIL)
    void shouldDeductBuyerPoints() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(post("/api/items/" + item.getId() + "/buy-with-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buyerPoints").value(10)));
    }

    @Test
    @WithMockUser(username = BUYER_EMAIL)
    void shouldCreditSellerPoints() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(post("/api/items/" + item.getId() + "/buy-with-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sellerPoints").value(15)));
    }
}