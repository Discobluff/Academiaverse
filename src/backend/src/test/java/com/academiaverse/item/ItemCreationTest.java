package com.academiaverse.item;

import com.academiaverse.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemCreationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createItemShouldReturnOk() {
        Item item = new Item();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setPrice(10.0);
        item.setType("sale");

        ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
