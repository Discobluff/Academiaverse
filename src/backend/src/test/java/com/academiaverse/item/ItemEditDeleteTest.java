package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class ItemEditDeleteTest {

    private String sellerMail = "seller@example.com";
    private String otherMail = "other@example.com";

    @Autowired
    private Helper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUpdateItem() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        Item updated = testHelper.createBaseItem(sellerMail);
        updated.setTitle("Updated Item");
        testHelper.testPut(sellerMail, "/api/items/" + item.getId(), MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(updated), status().isOk());
        testHelper.deleteItem(item);
        testHelper.deleteItem(updated);

    }

    @Test
    void shouldDeleteItem() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testDelete(sellerMail, "/api/items/" + item.getId(), status().isOk());
    }

    @Test
    void shouldNotDeleteIfNotOwner() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testDelete(otherMail, "/api/items/" + item.getId(), status().isForbidden());
        testHelper.deleteItem(item);
    }
}
