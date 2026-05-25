package com.academiaverse.saleconfirmation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.entity.Item;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class SaleConfirmationTest {

    private String sellerMail = "seller-sc@example.com";
    private String buyerMail = "buyer-sc@example.com";

    @Autowired
    private Helper testHelper;

    @Test
    void shouldSetOwnerConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldSetBuyerConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusNone() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusNone() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusPendingYou() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusPendingYou() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusBothConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusBothConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-popup", params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldReturnErrorForNonExistentItem() throws Exception {
        Long nonExistentId = 99999L;
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + nonExistentId + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorForNonExistentItemGetOwnerPopup() throws Exception {
        Long nonExistentId = 99999L;
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + nonExistentId + "/owner-popup", params, status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorForNonExistentItemGetBuyerPopup() throws Exception {
        Long nonExistentId = 99999L;
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, "/api/sale-confirmation/" + nonExistentId + "/buyer-popup", params, status().isInternalServerError());
    }

    @Test
    void multipleUsersCanConfirmForDifferentItems() throws Exception {
        Item item1 = testHelper.createBaseItem(sellerMail);
        Item item2 = testHelper.createBaseItem(buyerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item1.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item2.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item1);
        testHelper.deleteItemWithConfirmation(item2);
    }

    @Test
    void shouldAllowMultipleConfirmationsForSameItem() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(sellerMail, "/api/sale-confirmation/" + item.getId() + "/owner-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, "/api/sale-confirmation/" + item.getId() + "/buyer-confirmed", MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }
}