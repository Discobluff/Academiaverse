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

    public List<Interest> getInterestsForItem(Item item) {
        return interestRepository.findByItem(item);
    }

    @Transactional
    public Interest createInterest(User buyer, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<Interest> existing = interestRepository.findByUserAndItem(buyer, item);
        if (!existing.isEmpty()) {
            throw new RuntimeException("You already expressed interest in this item.");
        }

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

        interestRepository.deleteAll(existing);
    }
}