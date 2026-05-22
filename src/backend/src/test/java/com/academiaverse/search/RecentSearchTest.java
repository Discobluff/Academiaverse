package com.academiaverse.search;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.academiaverse.TestHelper;
import com.academiaverse.TestParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestHelper.class)
class RecentSearchTest {
    
    @Autowired
    private TestHelper testHelper;

    private String url = "/api/search/recent";

    private String mail = "searchuser@example.com";

    @Test
    void saveAndRetrieveRecentSearches() throws Exception {
        TestParam softwareParam = new TestParam("query", "Software Engineering");
        testHelper.testPost(mail, url, List.of(softwareParam), MediaType.APPLICATION_JSON, "", status().isOk());
        TestParam algorithmsParam = new TestParam("query", "Algorithms");
        testHelper.testPost(mail, url, List.of(algorithmsParam), MediaType.APPLICATION_JSON, "", status().isOk());

        testHelper.testGet(mail, url, List.of(), status().isOk(),
                jsonPath("$[0]").value("Algorithms"),
                jsonPath("$[1]").value("Software Engineering"));
    }

    @Test
    void recentEndpointWithoutAuthReturns403() throws Exception {
        testHelper.testGet(url, List.of(), status().isForbidden());
    }
}
