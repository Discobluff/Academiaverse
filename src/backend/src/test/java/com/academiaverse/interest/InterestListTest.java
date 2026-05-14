package com.academiaverse.interest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.academiaverse.entity.Interest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.InterestRepository;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InterestListTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User buyer;
    private Item item;

    @BeforeEach
    void setUp() {
        buyer = userRepository.findByEmail("buyer@example.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("buyer@example.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            u.setFirstname("Buyer");
            u.setLastname("User");
            return userRepository.save(u);
        });

        // Seller is only needed for the item owner – create it inline
        item = new Item();
        item.setTitle("Interesting Item");
        item.setDescription("An item");
        item.setPrice(10.0);
        item.setType("sale");
        item.setPaymentMethod("cash");
        item.setOwner(userRepository.findByEmail("seller@example.com").orElseGet(() -> {
            User s = new User();
            s.setEmail("seller@example.com");
            s.setPassword(passwordEncoder.encode("password"));
            s.setRole("ROLE_USER");
            s.setFirstname("Seller");
            s.setLastname("User");
            return userRepository.save(s);
        }));
        item = itemRepository.save(item);

        Interest interest = new Interest();
        interest.setItem(item);
        interest.setUser(buyer);
        interestRepository.save(interest);
    }

    @Test
    @WithMockUser(username = "buyer@example.com")
    void shouldReturnBuyerInterests() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/interests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.title").value("Interesting Item")));
    }
}