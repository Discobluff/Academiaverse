package com.academiaverse.repository;

import com.academiaverse.entity.Interest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByUser(User user);

    List<Interest> findByUserAndItem(User user, Item item);

    long countByItem(Item item);
}