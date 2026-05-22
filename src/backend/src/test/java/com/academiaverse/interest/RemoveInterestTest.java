package com.academiaverse.interest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import java.util.List;
import java.util.Map;
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
class RemoveInterestTest {

    @Autowired
    private Helper testHelper;

    private String buyerEmail = "buyerremove@example.com";
    private String sellerEmail = "sellerremove@example.com";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRemoveInterest() throws Exception {
        Item item = testHelper.createBaseItem(sellerEmail);
        Map<String, Long> body = Map.of("itemId", item.getId());

        testHelper.testPost(buyerEmail, "/api/interests", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(body), status().isOk());
        testHelper.testDelete(buyerEmail, "/api/interests/" + item.getId(), status().isOk());
        testHelper.testGet(buyerEmail, "/api/interests", List.of(), status().isOk(), jsonPath("$.length()").value(0));
    }
}