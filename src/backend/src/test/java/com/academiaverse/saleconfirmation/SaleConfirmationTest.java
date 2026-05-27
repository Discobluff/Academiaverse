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

    private static final String SALE_CONFIRMATION_BASE = "/api/sale-confirmation/";
    private static final String OWNER_CONFIRMED = "/owner-confirmed";
    private static final String BUYER_CONFIRMED = "/buyer-confirmed";
    private static final String OWNER_POPUP = "/owner-popup";
    private static final String BUYER_POPUP = "/buyer-popup";

    private String sellerMail = "seller-sc@example.com";
    private String buyerMail = "buyer-sc@example.com";

    @Autowired
    private Helper testHelper;

    @Test
    void shouldSetOwnerConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldSetBuyerConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusNone() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusNone() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusPendingYou() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusPendingYou() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetOwnerPopupStatusBothConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldGetBuyerPopupStatusBothConfirmed() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_POPUP, params, status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }

    @Test
    void shouldReturnErrorForNonExistentItem() throws Exception {
        Long nonExistentId = 99999L;
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + nonExistentId + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorForNonExistentItemGetOwnerPopup() throws Exception {
        Long nonExistentId = 99999L;
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + nonExistentId + OWNER_POPUP, params, status().isInternalServerError());
    }

    @Test
    void shouldReturnErrorForNonExistentItemGetBuyerPopup() throws Exception {
        Long nonExistentId = 99999L;
        List<com.academiaverse.Param> params = new ArrayList<>();
        testHelper.testGet(sellerMail, SALE_CONFIRMATION_BASE + nonExistentId + BUYER_POPUP, params, status().isInternalServerError());
    }

    @Test
    void multipleUsersCanConfirmForDifferentItems() throws Exception {
        Item item1 = testHelper.createBaseItem(sellerMail);
        Item item2 = testHelper.createBaseItem(buyerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item1.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item2.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item1);
        testHelper.deleteItemWithConfirmation(item2);
    }

    @Test
    void shouldAllowMultipleConfirmationsForSameItem() throws Exception {
        Item item = testHelper.createBaseItem(sellerMail);
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(sellerMail, SALE_CONFIRMATION_BASE + item.getId() + OWNER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.testPut(buyerMail, SALE_CONFIRMATION_BASE + item.getId() + BUYER_CONFIRMED, MediaType.APPLICATION_JSON, "", status().isOk());
        testHelper.deleteItemWithConfirmation(item);
    }
}