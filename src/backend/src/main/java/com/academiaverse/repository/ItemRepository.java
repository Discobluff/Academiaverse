package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Iterable<Item> findByOwner(User owner);
}
