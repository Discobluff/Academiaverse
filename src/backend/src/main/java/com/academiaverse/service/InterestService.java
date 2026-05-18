package com.academiaverse.service;

import com.academiaverse.entity.Interest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.InterestRepository;
import com.academiaverse.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepository interestRepository;
    private final ItemRepository itemRepository;

    public List<Interest> getInterestsForUser(User user) {
        return interestRepository.findByUser(user);
    }

    @Transactional
    public Interest createInterest(User buyer, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<Interest> existing = interestRepository.findByUserAndItem(buyer, item);
        if (!existing.isEmpty()) {
            throw new RuntimeException("You already expressed interest in this item.");
        }

        // Set item status to "Interest"
        item.setStatus("Interest");
        itemRepository.save(item);

        Interest interest = new Interest();
        interest.setItem(item);
        interest.setUser(buyer);
        return interestRepository.save(interest);
    }

    @Transactional
    public void removeInterest(User buyer, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<Interest> existing = interestRepository.findByUserAndItem(buyer, item);
        if (existing.isEmpty()) {
            throw new RuntimeException("No interest found for this item.");
        }

        // Delete all matching interests (clean duplicates)
        interestRepository.deleteAll(existing);

        // If no other interests for this item, reset its status
        long remaining = interestRepository.countByItem(item);
        if (remaining == 0) {
            item.setStatus(null);
            itemRepository.save(item);
        }
    }
}