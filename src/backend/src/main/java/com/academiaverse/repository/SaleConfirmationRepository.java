package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.SaleConfirmation;
import com.academiaverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SaleConfirmationRepository extends JpaRepository<SaleConfirmation, Long> {
    Optional<SaleConfirmation> findByItem(Item item);
    List<SaleConfirmation> findAllByBuyer(User buyer);
    void deleteByItem(Item item);
}
