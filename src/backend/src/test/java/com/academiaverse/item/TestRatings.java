package com.academiaverse.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.academiaverse.Helper;
import com.academiaverse.Param;
import com.academiaverse.dto.request.RatingRequest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.Rating;
import com.academiaverse.entity.Transaction;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.RatingRepository;
import com.academiaverse.repository.TransactionRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(Helper.class)
class TestRatings {

    private static final String mailSeller = "sellerRatings@example.com";
    private static final String mailBuyer = "buyerRatings@example.com";

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Helper testHelper;
    
    private Item item;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        item = testHelper.createBaseItem(mailSeller);
    }

    @AfterEach
    void cleanUp() {
        testHelper.deleteItem(item);
    }

    @Test
    void rateItemWithoutBought() throws Exception {
        RatingRequest request = new RatingRequest(item.getId(), 5);
        testHelper.testPost(mailBuyer, "/api/ratings", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(request), status().isForbidden());
    }
    
    @Test
    void getUserRatingEmpty() throws Exception {
        testHelper.testGet(mailBuyer, "/api/ratings/user", List.of(new Param("itemId", String.valueOf(item.getId()))), status().isNotFound());
    }

    @Transactional
    @Test
    void rateItem() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setBuyer(testHelper.getUser(mailBuyer));
        transactionRepository.save(transaction);

        RatingRequest request = new RatingRequest(item.getId(), 5);
        testHelper.testPost(mailBuyer, "/api/ratings", MediaType.APPLICATION_JSON, objectMapper.writeValueAsString(request), status().isOk());

        ratingRepository.deleteByItemAndUser(item, testHelper.getUser(mailBuyer));
        testHelper.deleteTransaction(item, testHelper.getUser(mailBuyer));
    }

    @Test
    void getUserRating() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setBuyer(testHelper.getUser(mailBuyer));
        transactionRepository.save(transaction);
        itemRepository.save(item);

        Rating rating = new Rating();
        rating.setItem(item);
        rating.setUser(testHelper.getUser(mailBuyer));
        rating.setRatingValue(5);
        ratingRepository.save(rating);

        ResultMatcher checkRating = result -> {
            String json = result.getResponse().getContentAsString();
            Rating responseRating = objectMapper.readValue(json, Rating.class);
            assertEquals(responseRating.getRatingValue(), 5, "Rating value should be 5");
        };

        testHelper.testGet(mailBuyer, "/api/ratings/user", List.of(new Param("itemId", String.valueOf(item.getId()))), status().isOk(), checkRating);

        ratingRepository.delete(rating);
        testHelper.deleteTransaction(item, testHelper.getUser(mailBuyer));
    }

}
