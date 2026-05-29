package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.SaleConfirmation;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.SaleConfirmationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SaleConfirmationService {

    private final SaleConfirmationRepository saleConfirmationRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public SaleConfirmation setOwnerConfirmed(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        confirmation.setOwnerConfirmed(true);
        confirmation.setUpdatedAt(LocalDateTime.now());
        return saleConfirmationRepository.save(confirmation);
    }

    @Transactional
    public SaleConfirmation setBuyerConfirmed(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        confirmation.setBuyerConfirmed(true);
        confirmation.setUpdatedAt(LocalDateTime.now());
        return saleConfirmationRepository.save(confirmation);
    }

    @Transactional(readOnly = true)
    public String getOwnerPopupStatus(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        boolean ownerConfirmed = Boolean.TRUE.equals(confirmation.getOwnerConfirmed());
        boolean buyerConfirmed = Boolean.TRUE.equals(confirmation.getBuyerConfirmed());
        if (ownerConfirmed && buyerConfirmed) {
            return "BOTH_CONFIRMED";
        }
        if (buyerConfirmed && !ownerConfirmed) {
            return "PENDING_YOU";
        }
        return "NONE";
    }

    @Transactional(readOnly = true)
    public String getBuyerPopupStatus(Long itemId) {
        SaleConfirmation confirmation = getOrCreateForItem(itemId);
        boolean ownerConfirmed = Boolean.TRUE.equals(confirmation.getOwnerConfirmed());
        boolean buyerConfirmed = Boolean.TRUE.equals(confirmation.getBuyerConfirmed());
        if (ownerConfirmed && buyerConfirmed) {
            return "BOTH_CONFIRMED";
        }
        if (ownerConfirmed && !buyerConfirmed) {
            return "PENDING_YOU";
        }
        return "NONE";
    }

    private SaleConfirmation getOrCreateForItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Item not found"));
        return saleConfirmationRepository.findByItem(item)
                .orElseGet(() -> {
                    SaleConfirmation confirmation = new SaleConfirmation();
                    confirmation.setItem(item);
                    confirmation.setCreatedAt(LocalDateTime.now());
                    confirmation.setUpdatedAt(LocalDateTime.now());
                    return confirmation;
                });
    }
}