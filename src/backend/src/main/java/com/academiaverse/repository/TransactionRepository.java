package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.Transaction;
import com.academiaverse.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByItemAndBuyer(Item item, User buyer);
    
    void deleteByItemAndBuyer(Item item, User buyer);

    List<Transaction> findByBuyer(User buyer);

    List<Transaction> findByItem(Item item);
}