package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.TestHelper;
import com.academiaverse.TestParam;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestHelper.class)
class DigitalProductUploadTest {

    private String mail = "digital@example.com";

    @Autowired
    private TestHelper testHelper;

    @Test
    void shouldUploadDigitalProduct() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture-notes.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "fake pdf content".getBytes(StandardCharsets.UTF_8)
        );

        testHelper.testMultipart(mail, "/api/items/upload", file, List.of(
            new TestParam("title", "Awesome Lecture Notes"),
            new TestParam("description", "Scanned notes from Lecture 1"),
            new TestParam("notes", "Includes practice problems"),
            new TestParam("price", "5"),
            new TestParam("type", "sale"),
            new TestParam("paymentMethod", "points")),
            status().isOk(),
            jsonPath("$.title").value("Awesome Lecture Notes"),
            jsonPath("$.isDigital").value(true),
            jsonPath("$.digitalFileName").value("lecture-notes.pdf"),
            jsonPath("$.digitalFilePath").isNotEmpty()
        );
    }
}
