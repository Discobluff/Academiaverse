package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.Rating;
import com.academiaverse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Rating findByUserAndItem(User user, Item item);

    List<Rating> findByItem(Item item);

    void deleteByItemAndUser(Item item, User user);
}