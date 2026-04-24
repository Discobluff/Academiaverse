package com.academiaverse.controller;

import com.academiaverse.entity.Course;
import com.academiaverse.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String q) {
        if (q == null || q.length() < 2) {
            return List.of();
        }
        return courseRepository.findByNameContainingIgnoreCase(q)
                .stream()
                .map(Course::getName)
                .limit(5)
                .collect(Collectors.toList());
    }
}
