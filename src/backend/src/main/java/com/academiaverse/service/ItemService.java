package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ChatRepository;
import com.academiaverse.repository.ItemRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ChatRepository chatRepository;
    private static final String UPLOAD_DIR = "uploads/";

    @Transactional
    public Item createItem(Item item, User owner) {
        item.setOwner(owner);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return itemRepository.save(item);
    }

    @Transactional
    public Item createDigitalItem(Item item, User owner, MultipartFile file) {
        item.setOwner(owner);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setIsDigital(true);
        item.setDigitalFileName(file.getOriginalFilename());

        // Save file to disk
        saveFile(file, item.getDigitalFileName());

        item.setDigitalFilePath("/api/uploads/" + file.getOriginalFilename());
        return itemRepository.save(item);
    }

    @Transactional
    public Item createPhysicalItemWithImage(Item item, User owner, MultipartFile file) {
        item.setOwner(owner);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setIsDigital(false);
        item.setDigitalFileName(file.getOriginalFilename());

        // Save image to disk
        saveFile(file, item.getDigitalFileName());

        item.setDigitalFilePath("/api/uploads/" + file.getOriginalFilename());
        return itemRepository.save(item);
    }

    private void saveFile(MultipartFile file, String filename) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + filename, e);
        }
    }

    public Iterable<Item> getSellerListings(User seller) {
        return itemRepository.findByOwner(seller);
    }

    @Transactional
    public Item updateItem(Long id, Item updated, User owner) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You can only edit your own items");
        }
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setNotes(updated.getNotes());
        existing.setPrice(updated.getPrice());
        existing.setType(updated.getType());
        existing.setPaymentMethod(updated.getPaymentMethod());
        existing.setAvailability(updated.getAvailability());
        existing.setAvailableUntil(updated.getAvailableUntil());
        existing.setUpdatedAt(LocalDateTime.now());
        return itemRepository.save(existing);
    }

    @Transactional
    public void deleteItem(Long id, User owner) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You can only delete your own items");
        }
        // Delete associated chat messages first
        chatRepository.deleteByItemId(id);
        itemRepository.delete(existing);
    }
}