package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.SaleConfirmation;
import com.academiaverse.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleConfirmationRepository extends JpaRepository<SaleConfirmation, Long> {
    Optional<SaleConfirmation> findByItem(Item item);

    List<SaleConfirmation> findAllByBuyer(User buyer);

    void deleteByItem(Item item);
}
