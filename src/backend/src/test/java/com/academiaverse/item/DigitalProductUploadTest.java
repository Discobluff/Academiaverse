package com.academiaverse.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DigitalProductUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("digital@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("digital@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("USER");
            user.setFirstname("Digital");
            user.setLastname("Tester");
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "digital@example.com")
    void shouldUploadDigitalProduct() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture-notes.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "fake pdf content".getBytes(StandardCharsets.UTF_8)
        );

        assertDoesNotThrow(() -> mockMvc.perform(multipart("/api/items/upload")
                .file(file)
                .param("title", "Awesome Lecture Notes")
                .param("description", "Scanned notes from Lecture 1")
                .param("price", "5")
                .param("type", "sale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Awesome Lecture Notes"))
                .andExpect(jsonPath("$.isDigital").value(true))
                .andExpect(jsonPath("$.digitalFileName").value("lecture-notes.pdf"))
                .andExpect(jsonPath("$.digitalFilePath").isNotEmpty()));
    }
}
