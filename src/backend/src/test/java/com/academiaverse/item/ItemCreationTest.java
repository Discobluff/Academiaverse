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
class ItemCreationTest {

    private String mail = "testadditem@example.com";

    @Autowired
    private Helper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItemShouldReturnOk() throws Exception {
        Item item = testHelper.createBaseItem(mail);
        String json = objectMapper.writeValueAsString(item);
        testHelper.testPost(mail, "/api/items", MediaType.APPLICATION_JSON, json, status().isOk());
        testHelper.deleteItem(item);
    }
}
