package com.academiaverse.controller;

import com.academiaverse.entity.SaleConfirmation;
import com.academiaverse.service.SaleConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale-confirmation")
public class SaleConfirmationController {

    @Autowired
    private SaleConfirmationService saleConfirmationService;

    @PutMapping("/{id}/owner-confirmed")
    public ResponseEntity<SaleConfirmation> setOwnerConfirmed(@PathVariable Long id) {
        SaleConfirmation confirmation = saleConfirmationService.setOwnerConfirmed(id);
        return ResponseEntity.ok(confirmation);
    }

    @PutMapping("/{id}/buyer-confirmed")
    public ResponseEntity<SaleConfirmation> setBuyerConfirmed(@PathVariable Long id, @RequestParam boolean confirmed) {
        SaleConfirmation saleConfirmation = saleConfirmationService.setBuyerConfirmed(id, confirmed);
        return ResponseEntity.ok(saleConfirmation);
    }

    @GetMapping("/{id}/owner-popup")
    public ResponseEntity<Boolean> getShouldOwnerGetPopup(@PathVariable Long id) {
        boolean shouldGetPopup = saleConfirmationService.shouldOwnerGetPopup(id);
        return ResponseEntity.ok(shouldGetPopup);
    }

    @GetMapping("/{id}/buyer-popup")
    public ResponseEntity<Boolean> getShouldBuyerGetPopup(@PathVariable Long id) {
        boolean shouldGetPopup = saleConfirmationService.shouldBuyerGetPopup(id);
        return ResponseEntity.ok(shouldGetPopup);
    }
}




