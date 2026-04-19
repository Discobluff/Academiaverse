package com.academiaverse.controller;

import com.academiaverse.entity.Course;
import com.academiaverse.repository.CourseRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/search")
    public List<Course> search(@RequestParam String q) {
        return courseRepository.findByNameContainingIgnoreCase(q);
    }
}
