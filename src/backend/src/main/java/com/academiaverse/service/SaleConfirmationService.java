package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.SaleConfirmation;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.SaleConfirmationRepository;
import com.academiaverse.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleConfirmationService {

    private final SaleConfirmationRepository saleConfirmationRepository;
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public SaleConfirmation setOwnerConfirmed(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        confirmation.setOwnerConfirmed(true);
        confirmation.setUpdatedAt(LocalDateTime.now());
        return saleConfirmationRepository.save(confirmation);
    }

    @Transactional
    public SaleConfirmation setBuyerConfirmed(Long itemId, boolean confirmed) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        confirmation.setBuyerConfirmed(confirmed);
        confirmation.setUpdatedAt(LocalDateTime.now());
        return saleConfirmationRepository.save(confirmation);
    }

    @Transactional(readOnly = true)
    public boolean shouldOwnerGetPopup(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        return Boolean.TRUE.equals(confirmation.getBuyerConfirmed())
            && !Boolean.TRUE.equals(confirmation.getOwnerConfirmed());
    }

    @Transactional(readOnly = true)
    public boolean shouldBuyerGetPopup(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        return Boolean.TRUE.equals(confirmation.getOwnerConfirmed())
            && !Boolean.TRUE.equals(confirmation.getBuyerConfirmed());
    }

    private SaleConfirmation getOrCreateForItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        Optional<SaleConfirmation> confirmation = saleConfirmationRepository.findByItem(item);
        return confirmation.orElseGet(() -> createForItem(item));
    }

    private SaleConfirmation createForItem(Item item) {
        SaleConfirmation confirmation = new SaleConfirmation();
        confirmation.setItem(item);
        confirmation.setBuyer(resolveBuyer(item));
        confirmation.setOwnerConfirmed(false);
        confirmation.setBuyerConfirmed(false);
        confirmation.setCreatedAt(LocalDateTime.now());
        confirmation.setUpdatedAt(LocalDateTime.now());
        return saleConfirmationRepository.save(confirmation);
    }

    private User resolveBuyer(Item item) {
        return transactionRepository.findByItem(item)
            .stream()
            .findFirst()
            .map(transaction -> transaction.getBuyer())
            .orElse(null);
    }
}
