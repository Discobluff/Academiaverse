package com.academiaverse.search;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.User;
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
class RecentSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String url = "/api/search/recent";

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("searchuser@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("searchuser@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("USER");
            user.setFirstname("Search");
            user.setLastname("User");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "searchuser@example.com")
    void saveAndRetrieveRecentSearches() throws Exception {
        mockMvc.perform(post(url)
                .param("query", "Software Engineering")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(url)
                .param("query", "Algorithms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertDoesNotThrow(() -> mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Algorithms"))
                .andExpect(jsonPath("$[1]").value("Software Engineering")));
    }

    @Test
    void recentEndpointWithoutAuthReturns403() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(get(url))
                .andExpect(status().isForbidden()));
    }
}
