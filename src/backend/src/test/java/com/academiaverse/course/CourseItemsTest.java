package com.academiaverse.course;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.entity.Course;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.CourseRepository;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CourseItemsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Course course;
    private Item item;

    @BeforeEach
    void setUp() {
        User owner = userRepository.findByEmail("owner@example.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("owner@example.com");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            u.setFirstname("Owner");
            u.setLastname("User");
            return userRepository.save(u);
        });

        course = new Course();
        course.setName("Algorithms");
        course.setCode("ALG101");
        course = courseRepository.save(course);

        item = new Item();
        item.setTitle("Algorithm Book");
        item.setDescription("A great book");
        item.setPrice(10.0);
        item.setType("sale");
        item.setPaymentMethod("cash");
        item.setOwner(owner);
        item.setCourse(course);
        itemRepository.save(item);
    }

    @Test
    void shouldReturnItemsForCourse() throws Exception {
        mockMvc.perform(get("/api/courses/" + course.getId() + "/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Algorithm Book"))
                .andExpect(jsonPath("$[0].course.id").value(course.getId().intValue()));
    }
}