package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

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
        // In a real app, you'd save to a storage service. For now, just store the reference.
        item.setDigitalFilePath("/uploads/" + file.getOriginalFilename());
        return itemRepository.save(item);
    }
}
