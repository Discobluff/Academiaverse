package com.academiaverse.Auth;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TestLogin extends TestAuth{

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testLoginInexistentUser() throws Exception {
        String body = """
            {
                "username": "userInexistent",
                "password": "test",
            }
            """;

        login(body, status().isBadRequest());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        int randomId = (int) (Math.random() * 1000);
        String body = """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        register(body, status().isOk());
        String loginBody = """
            {
                "username": "test%d@test.com",
                "password": "wrongPassword"
            }
            """.formatted(randomId);
        login(loginBody, status().isUnauthorized());
    }


    @Test
    void testLoginSuccess() throws Exception {
    int randomId = (int) (Math.random() * 1000);
        String body = """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        register(body, status().isOk());
        String loginBody = """
            {
                "username": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        login(loginBody, status().isOk());
    }
}