package com.academiaverse.interest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class RemoveInterestTest {

    @Autowired
    private Helper testHelper;

    private String buyer_email = "buyerremove@example.com";
    private String seller_email = "sellerremove@example.com";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRemoveInterest() throws Exception {
        Item item = testHelper.createBaseItem(seller_email);
        Map<String, Long> body = Map.of("itemId", item.getId());

        testHelper.testPost(buyer_email, "/api/interests", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(body), status().isOk());
        testHelper.testDelete(buyer_email, "/api/interests/" + item.getId(), status().isOk());
        testHelper.testGet(buyer_email, "/api/interests", List.of(), status().isOk(), jsonPath("$.length()").value(0));
    }
}