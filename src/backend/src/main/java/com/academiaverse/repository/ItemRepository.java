package com.academiaverse.repository;

import com.academiaverse.entity.Course;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Iterable<Item> findByOwner(User owner);

    List<Item> findByCourse(Course course);
}