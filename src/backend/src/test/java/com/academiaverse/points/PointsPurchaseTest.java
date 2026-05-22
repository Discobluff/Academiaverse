package com.academiaverse.points;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.TestHelper;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestHelper.class)
class PointsPurchaseTest {

    private String buyer_email = "buyerpoints@example.com";
    private String seller_email = "sellerpoints@example.com";

    @Autowired
    private TestHelper testHelper;

    @Test
    void shouldBuyWithPointsReturnsOk() throws Exception {
        Item item = testHelper.createBaseItem(seller_email);
        testHelper.testPost(buyer_email, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", status().isOk());
    }

    @Test
    void shouldDeductBuyerPoints() throws Exception {
        testHelper.setPoints(seller_email, 10);
        testHelper.setPoints(buyer_email, 10);
        User buyer = testHelper.getUser(buyer_email);
        Item item = testHelper.createBaseItem(seller_email);
        Integer newPoints = buyer.getPoints() - item.getPrice().intValue();

        testHelper.testPost(buyer_email, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", jsonPath("$.buyerPoints").value(newPoints));
    }

    @Test
    void shouldCreditSellerPoints() throws Exception {
        testHelper.setPoints(seller_email, 10);
        testHelper.setPoints(buyer_email, 10);
        User seller = testHelper.getUser(seller_email);
        Item item = testHelper.createBaseItem(seller_email);
        Integer newPoints = seller.getPoints() + item.getPrice().intValue();

        testHelper.testPost(buyer_email, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", jsonPath("$.sellerPoints").value(newPoints));
    }
}