package com.academiaverse.controller;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final ItemService itemService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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

    @GetMapping("/seller_listings")
    public ResponseEntity<Iterable<Item>> getSellerListings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Iterable<Item> listings = itemService.getSellerListings(user);
        return ResponseEntity.ok(listings);
    }
}