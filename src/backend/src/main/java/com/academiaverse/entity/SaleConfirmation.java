package com.academiaverse.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saleConfirmation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties("saleConfirmation")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private Boolean ownerConfirmed = false;
    private Boolean buyerConfirmed = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}