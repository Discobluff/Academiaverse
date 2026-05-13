package com.academiaverse.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.Course;
import com.academiaverse.entity.User;
import com.academiaverse.repository.CourseRepository;
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
class ItemCourseAssociationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Course course;

    @BeforeEach
    void setUp() {
        // Create seller user (needed for the mock authentication)
        if (userRepository.findByEmail("seller@example.com").isEmpty()) {
            User seller = new User();
            seller.setEmail("seller@example.com");
            seller.setPassword(passwordEncoder.encode("password"));
            seller.setRole("ROLE_USER");
            seller.setFirstname("Seller");
            seller.setLastname("User");
            userRepository.save(seller);
        }

        course = new Course();
        course.setName("Software Engineering");
        course.setCode("SE101");
        course = courseRepository.save(course);
    }

    @Test
    @WithMockUser(username = "seller@example.com")
    void shouldCreateItemWithCourse() throws Exception {
        Map<String, Object> body = Map.of(
            "title", "Design Patterns Book",
            "description", "Useful for SWE",
            "price", 20.0,
            "type", "sale",
            "paymentMethod", "cash",
            "course", Map.of("id", course.getId())
        );

        assertDoesNotThrow(() -> mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course.id").value(course.getId().intValue()))
                .andExpect(jsonPath("$.course.name").value("Software Engineering")));
    }
}