package com.academiaverse.repository;

import com.academiaverse.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> findByNameContainingIgnoreCase(@Param("query") String query);
}