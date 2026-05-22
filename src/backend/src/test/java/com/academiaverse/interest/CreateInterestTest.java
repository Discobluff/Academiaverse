package com.academiaverse.interest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
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
class CreateInterestTest {

    @Autowired
    private Helper testHelper;

    private String mail = "buyer14@example.com";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateInterestAndChangeItemStatus() throws Exception {
        Item item = testHelper.createBaseItem(mail);
        Map<String, Long> body = Map.of("itemId", item.getId());

        testHelper.testPost(mail, "/api/interests", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(body), status().isOk(), jsonPath("$.item.status").value("Interest"), jsonPath("$.item.id").value(item.getId().intValue()));
    }
}