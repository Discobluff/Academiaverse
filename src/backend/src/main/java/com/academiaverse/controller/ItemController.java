package com.academiaverse.controller;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_NOT_FOUND = "User not found";

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Item saved = itemService.createItem(item, user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload")
    public ResponseEntity<Item> uploadDigitalItem(@RequestParam("title") String title,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("notes") String notes,
                                                  @RequestParam("price") Double price,
                                                  @RequestParam("type") String type,
                                                  @RequestParam("paymentMethod") String paymentMethod,
                                                  @RequestParam("file") MultipartFile file,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setNotes(notes);
        item.setPrice(price);
        item.setType(type);
        item.setPaymentMethod(paymentMethod);

        Item saved = itemService.createDigitalItem(item, user, file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return ResponseEntity.ok(item);
    }

    @GetMapping("/seller_listings")
    public ResponseEntity<Iterable<Item>> getSellerListings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Iterable<Item> listings = itemService.getSellerListings(user);
        return ResponseEntity.ok(listings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id,
                                           @Valid @RequestBody Item updatedItem,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Item saved = itemService.updateItem(id, updatedItem, user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!item.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        itemService.deleteItem(id, user);
        return ResponseEntity.ok().build();
    }
}
