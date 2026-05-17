package com.academiaverse.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ReportAdminListTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.academiaverse.repository.UserRepository userRepository;

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            com.academiaverse.entity.User admin = new com.academiaverse.entity.User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ADMIN");
            admin.setFirstname("Admin");
            admin.setLastname("User");
            userRepository.save(admin);
        }
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    void shouldReturnReportsForAdmin() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk()));
    }
}