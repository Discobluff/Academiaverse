package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.Transaction;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.TransactionRepository;
import com.academiaverse.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointsService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Map<String, Integer> buyWithPoints(User buyer, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!"points".equals(item.getPaymentMethod())) {
            throw new RuntimeException("This item cannot be purchased with points");
        }

        // Prevent duplicate purchase
        if (transactionRepository.findByItemAndBuyer(item, buyer).isPresent()) {
            throw new RuntimeException("You have already purchased this item.");
        }

        int price = item.getPrice().intValue();

        int buyerPoints = buyer.getPoints() != null ? buyer.getPoints() : 0;
        if (buyerPoints < price) {
            throw new RuntimeException("Not enough points");
        }

        User seller = item.getOwner();
        int sellerPoints = seller.getPoints() != null ? seller.getPoints() : 0;

        buyer.setPoints(buyerPoints - price);
        seller.setPoints(sellerPoints + price);

        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setBuyer(buyer);
        transactionRepository.save(transaction);

        // Mark the item as Sold (optional – keeps existing behaviour)
        item.setStatus("Sold");
        itemRepository.save(item);

        userRepository.save(buyer);
        userRepository.save(seller);

        return Map.of("buyerPoints", buyer.getPoints(), "sellerPoints", seller.getPoints());
    }
}