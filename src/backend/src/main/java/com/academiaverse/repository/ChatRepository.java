package com.academiaverse.repository;

import com.academiaverse.entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.item.owner.id = :userId OR c.sender.id = :userId OR c.buyer.id = :userId ORDER BY c.createdAt ASC")
    List<Chat> findByUser(@Param("userId") Integer userId);

    void deleteByItemId(Long itemId);
}