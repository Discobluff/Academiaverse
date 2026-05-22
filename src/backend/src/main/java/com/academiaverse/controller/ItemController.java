package com.academiaverse.controller;

import com.academiaverse.entity.Course;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.CourseRepository;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.TransactionRepository;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ItemService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String ITEM_NOT_FOUND = "Item not found";    

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/bought")
    public ResponseEntity<Boolean> hasBoughtItem(@RequestParam Long itemId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
        if (transactionRepository.findByItemAndBuyer(item, user).isEmpty()) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        if (item.getCourse() != null && item.getCourse().getId() != null) {
            Course course = courseRepository.findById(item.getCourse().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
            item.setCourse(course);
        }
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
                                                  @RequestParam(value = "courseId", required = false) Long courseId,
                                                  @RequestParam(value = "isDigital", defaultValue = "true") boolean isDigital,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
                                                          
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setNotes(notes);
        item.setPrice(price);
        item.setType(type);
        item.setPaymentMethod(paymentMethod);

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
            item.setCourse(course);
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Item saved;
        if (isDigital) {
            saved = itemService.createDigitalItem(item, user, file);
        } else {
            saved = itemService.createPhysicalItemWithImage(item, user, file);
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
        if (!item.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        itemService.deleteItem(id, user);
        return ResponseEntity.ok().build();
    }

    // ---------- NEW: Digital product download ----------
    @GetMapping("/{id}/download")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadItem(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));

        // Only the buyer can download
        if (transactionRepository.findByItemAndBuyer(item, user).isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Only digital items can be downloaded
        if (!item.getIsDigital() || item.getDigitalFileName() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path filePath = Paths.get("uploads/", item.getDigitalFileName());
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + item.getDigitalFileName() + "\"")
                .body(data);
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<Item>> getPurchases(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        List<Item> purchased = transactionRepository.findByBuyer(user).stream()
                .map(transaction -> transaction.getItem())
                .collect(Collectors.toList());
        return ResponseEntity.ok(purchased);
    }    
}