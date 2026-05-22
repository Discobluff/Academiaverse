package com.academiaverse.report;

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
class ReportCreationTest {

    @Autowired
    private Helper testHelper;

    @Autowired
    private ObjectMapper objectMapper;

    private String reporterEmail = "reporter@example.com";
    private String ownerEmail = "owner@example.com";

    @Test
    void shouldCreateReport() throws Exception {
        Item item = testHelper.createBaseItem(ownerEmail);
        Map<String, Object> body = Map.of(
                "itemId", item.getId(),
                "reason", "Inappropriate content"
        );

        testHelper.testPost(reporterEmail, "/api/reports", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(body), status().isOk());
    }
}