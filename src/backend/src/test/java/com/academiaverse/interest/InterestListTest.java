package com.academiaverse.interest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Interest;
import com.academiaverse.entity.Item;
import com.academiaverse.repository.InterestRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class InterestListTest {

    @Autowired
    private Helper testHelper;

    private String mailSeller = "seller@example.com";
    private String mailBuyer = "buyer@example.com";

    @Autowired
    private InterestRepository interestRepository;

    @Test
    void shouldReturnBuyerInterests() throws Exception {
        Item item = testHelper.createBaseItem(mailSeller);
        Interest interest = new Interest();
        interest.setItem(item);
        interest.setUser(testHelper.getUser(mailBuyer));
        interestRepository.save(interest);

        testHelper.testGet(mailBuyer, "/api/interests", List.of(),
                status().isOk(),
                jsonPath("$[*].item.title", hasItem(item.getTitle())));

        interestRepository.delete(interest);
    }
}