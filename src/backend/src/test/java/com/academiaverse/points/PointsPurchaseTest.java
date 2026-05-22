package com.academiaverse.points;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class PointsPurchaseTest {

    private String buyerEmail = "buyerpoints@example.com";
    private String sellerEmail = "sellerpoints@example.com";

    @Autowired
    private Helper testHelper;

    @Test
    void shouldBuyWithPointsReturnsOk() throws Exception {
        Item item = testHelper.createBaseItem(sellerEmail);
        testHelper.testPost(buyerEmail, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", status().isOk());
    }

    @Test
    void shouldDeductBuyerPoints() throws Exception {
        testHelper.setPoints(sellerEmail, 10);
        testHelper.setPoints(buyerEmail, 10);
        User buyer = testHelper.getUser(buyerEmail);
        Item item = testHelper.createBaseItem(sellerEmail);
        Integer newPoints = buyer.getPoints() - item.getPrice().intValue();

        testHelper.testPost(buyerEmail, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", jsonPath("$.buyerPoints").value(newPoints));
    }

    @Test
    void shouldCreditSellerPoints() throws Exception {
        testHelper.setPoints(sellerEmail, 10);
        testHelper.setPoints(buyerEmail, 10);
        User seller = testHelper.getUser(sellerEmail);
        Item item = testHelper.createBaseItem(sellerEmail);
        Integer newPoints = seller.getPoints() + item.getPrice().intValue();

        testHelper.testPost(buyerEmail, "/api/items/" + item.getId() + "/buy-with-points", MediaType.APPLICATION_JSON, "", jsonPath("$.sellerPoints").value(newPoints));
    }
}