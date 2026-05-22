package com.academiaverse.item;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class TestSellerListings {

    private static final String mail = "sellerlistings@example.com";

    @Autowired 
    private Helper testHelper;

    @Test
    void getSellerListingsShouldReturnOk() throws Exception {
        Item item = testHelper.createBaseItem(mail);
        testHelper.testGet(mail, "/api/items/seller_listings", List.of(), status().isOk());
        testHelper.deleteItem(item);
    }

    @Test
    @WithMockUser(username = mail)
    void getSellerListings() throws Exception {
        
        Item item = testHelper.createBaseItem(mail);

        ResultMatcher[] matchers = new ResultMatcher[] {
            status().isOk(),
            jsonPath("$[0].title").value(item.getTitle()),
            jsonPath("$[0].description").value(item.getDescription()),
            jsonPath("$[0].price").value(item.getPrice()),
            jsonPath("$[0].type").value(item.getType()),
            jsonPath("$[0].notes").value(item.getNotes()),
            jsonPath("$[0].paymentMethod").value(item.getPaymentMethod())
        };

        testHelper.testGet(mail, "/api/items/seller_listings", List.of(), matchers);

        
        testHelper.deleteItem(item);
    }
}
