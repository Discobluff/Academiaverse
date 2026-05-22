package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.TestHelper;
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
@Import(TestHelper.class)
class ItemCreationTest {

    private String mail = "testadditem@example.com";

    @Autowired
    private TestHelper testHelper;

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
