package com.academiaverse.controller;

import com.academiaverse.entity.Course;
import com.academiaverse.entity.Item;
import com.academiaverse.repository.CourseRepository;
import com.academiaverse.repository.ItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courseRepository;
    private final ItemRepository itemRepository;

    public CourseController(CourseRepository courseRepository, ItemRepository itemRepository) {
        this.courseRepository = courseRepository;
        this.itemRepository = itemRepository;
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

    @GetMapping("/{id}/items")
    public List<Item> getCourseItems(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return itemRepository.findByCourse(course);
    }
}