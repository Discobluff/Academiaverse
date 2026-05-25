package com.academiaverse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.TransactionRepository;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.security.JwtService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class Helper {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private com.academiaverse.repository.SaleConfirmationRepository saleConfirmationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String headerAuth = "Authorization";
    private String bearer = "Bearer ";

    public void setAdmin(String mail) {
        User user = getUser(mail);
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    public void setPoints(String mail, Integer points) {
        User user = getUser(mail);
        user.setPoints(points);
        userRepository.save(user);
    }

    public User getUser(String mail) {
        User user = userRepository.findByEmail(mail).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(mail);
            user.setRole("USER");
            user.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public void deleteTransaction(Item item, User buyer) {
        transactionRepository.deleteByItemAndBuyer(item, buyer);
    }

    public void deleteItem(Item item) {
        itemRepository.delete(item);
    }

    @Transactional
    public void deleteItemWithConfirmation(Item item) {
        saleConfirmationRepository.deleteByItem(item);
        itemRepository.delete(item);
    }

    public Item createBaseItem(String mailSeller) {
        Item item = new Item();
        item.setTitle("Test Item");
        item.setDescription("Test Description");
        item.setOwner(getUser(mailSeller));
        item.setPrice(1.0);
        item.setType("sale");
        item.setNotes("Test notes");
        item.setPaymentMethod("points");
        return createItem(item);
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public void testDelete(String mailUser, String uri, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = delete(uri).header(headerAuth, bearer + token);
        ResultActions resultsActions = mockMvc.perform(request);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testPut(String mailUser, String uri, MediaType mediaType, String content, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = put(uri).header(headerAuth, bearer + token).contentType(mediaType).content(content);
        ResultActions resultsActions = mockMvc.perform(request);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testGet(String uri, List<Param> params, ResultMatcher... matchers) throws Exception {
        MockHttpServletRequestBuilder request = get(uri);
        for (Param param : params) {
            request = request.param(param.getName(), param.getValue());
        }
        final MockHttpServletRequestBuilder finalRequest = request;
        ResultActions resultsActions = mockMvc.perform(finalRequest);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }    

    public void testGet(String mailUser, String uri, List<Param> params, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = get(uri).header(headerAuth, bearer + token);
        for (Param param : params) {
            request = request.param(param.getName(), param.getValue());
        }
        final MockHttpServletRequestBuilder finalRequest = request;
        ResultActions resultsActions = mockMvc.perform(finalRequest);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testMultipart(String mailUser, String uri, MockMultipartFile file, List<Param> params, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockMultipartHttpServletRequestBuilder  request = multipart(uri).header(headerAuth, bearer + token).file(file);
        for (Param param : params) {
            request = request.param(param.getName(), param.getValue());
        }
        MockMultipartHttpServletRequestBuilder finalRequest = request;
        ResultActions resultsActions = mockMvc.perform(finalRequest);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testPost(String mailUser, String uri, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = post(uri).header(headerAuth, bearer + token);
        ResultActions resultsActions = mockMvc.perform(request);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testPost(String mailUser, String uri, MediaType mediaType, String content, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = post(uri).header(headerAuth, bearer + token).contentType(mediaType).content(content);
        ResultActions resultsActions = mockMvc.perform(request);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }

    public void testPost(String mailUser, String uri, List<Param> params, MediaType mediaType, String content, ResultMatcher... matchers) throws Exception {
        User user = getUser(mailUser);
        String token = jwtService.generateToken(user);
        MockHttpServletRequestBuilder request = post(uri).header(headerAuth, bearer + token).contentType(mediaType).content(content);
        for (Param param : params) {
            request = request.param(param.getName(), param.getValue());
        }
        ResultActions resultsActions = mockMvc.perform(request);
        for (ResultMatcher matcher : matchers) {
            assertDoesNotThrow(() -> resultsActions.andExpect(matcher));
        }
    }
}
