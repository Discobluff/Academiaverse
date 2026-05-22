package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Course;
import com.academiaverse.repository.CourseRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class ItemCourseAssociationTest {

    private String mail = "seller@example.com";

    @Autowired
    private Helper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;
    
    @Test
    void shouldCreateItemWithCourse() throws Exception {
        Course course = new Course();
        course.setName("Software Engineering");
        course.setCode("SE101");
        Course finalCourse = courseRepository.save(course);;
        Map<String, Object> body = Map.of(
            "title", "Design Patterns Book",
            "description", "Useful for SWE",
            "price", 20.0,
            "type", "sale",
            "paymentMethod", "cash",
            "course", Map.of("id", finalCourse.getId())
        );

        testHelper.testPost(mail, "/api/items", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(body), status().isOk(), 
            jsonPath("$.course.id").value(finalCourse.getId().intValue()),
            jsonPath("$.course.name").value("Software Engineering")
        );
    }
}