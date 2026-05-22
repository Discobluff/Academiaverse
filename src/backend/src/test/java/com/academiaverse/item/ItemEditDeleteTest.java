package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class ItemEditDeleteTest {

    private String seller_mail = "seller@example.com";
    private String other_mail = "other@example.com";

    @Autowired
    private Helper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUpdateItem() throws Exception {
        Item item = testHelper.createBaseItem(seller_mail);
        Item updated = testHelper.createBaseItem(seller_mail);
        updated.setTitle("Updated Item");
        testHelper.testPut(seller_mail, "/api/items/" + item.getId(), MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(updated), status().isOk());
        testHelper.deleteItem(item);
        testHelper.deleteItem(updated);

    }

    @Test
    void shouldDeleteItem() throws Exception {
        Item item = testHelper.createBaseItem(seller_mail);
        testHelper.testDelete(seller_mail, "/api/items/" + item.getId(), status().isOk());
    }

    @Test
    void shouldNotDeleteIfNotOwner() throws Exception {
        Item item = testHelper.createBaseItem(seller_mail);
        testHelper.testDelete(other_mail, "/api/items/" + item.getId(), status().isForbidden());
        testHelper.deleteItem(item);
    }
}
