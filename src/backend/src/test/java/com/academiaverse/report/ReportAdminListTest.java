package com.academiaverse.report;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class ReportAdminListTest {

    @Autowired
    private Helper testHelper;

    private String mail = "admin@example.com";

    @BeforeEach
    void setUp() {
        testHelper.setAdmin(mail);
    }

    @Test
    void shouldReturnReportsForAdmin() throws Exception {
        testHelper.testGet(mail, "/api/reports", List.of(), status().isOk());
    }
}