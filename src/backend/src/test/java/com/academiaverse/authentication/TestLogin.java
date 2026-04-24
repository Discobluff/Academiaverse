package com.academiaverse.authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestLogin extends Auth {

    private static final Random random = new Random();

    @Test
    void testLoginInexistentUser() throws Exception {
        String body =
            """
            {
                "username": "userInexistent",
                "password": "test",
            }
            """;

        assertDoesNotThrow(() -> login(body, status().isBadRequest()));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        int randomId = random.nextInt(1000);
        String body =
            """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        register(body, status().isOk());
        String loginBody =
            """
            {
                "username": "test%d@test.com",
                "password": "wrongPassword"
            }
            """.formatted(randomId);
        assertDoesNotThrow(() -> login(loginBody, status().isUnauthorized()));
    }

    @Test
    void testLoginSuccess() throws Exception {
        int randomId = random.nextInt(1000);
        String body =
            """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        register(body, status().isOk());
        String loginBody =
            """
            {
                "username": "test%d@test.com",
                "password": "test"
            }
            """.formatted(randomId);
        assertDoesNotThrow(() -> login(loginBody, status().isOk()));
    }
}