package com.academiaverse.report;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.TestHelper;
import com.academiaverse.entity.Item;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestHelper.class)
class ReportCreationTest {

    @Autowired
    private TestHelper testHelper;

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