package com.academiaverse.authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestRegister extends Auth {

    private static final Random random = new Random();

    @Test
    void testRegisterEndpoint() throws Exception {
        int randomId = random.nextInt(1000);
        String body =
            """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@example.com",
                "password": "test"
            }
            """.formatted(randomId);
        assertDoesNotThrow(() -> register(body, status().isOk()));
    }

    @Test
    void testRegisterWithoutEmail() throws Exception {
        String body =
            """
            {
                "firstname": "",
                "lastname": "",
                "email": "",
                "password": "test"
            }
            """;

        assertDoesNotThrow(() -> register(body, status().isBadRequest()));
    }

    @Test
    void testRegisterWithoutPassword() throws Exception {
        String body =
            """
            {
                "firstname": "",
                "lastname": "",
                "email": "test@example.com",
                "password": ""
            """;

        assertDoesNotThrow(() -> register(body, status().isBadRequest()));
    }

    @Test
    void testRegisterWithExistingEmail() throws Exception {
        int randomId = random.nextInt(1000);
        String body =
            """
            {
                "firstname": "Test",
                "lastname": "Test",
                "email": "test%d@example.com",
                "password": "test"
            }
            """.formatted(randomId);

        register(body, status().isOk());
        assertDoesNotThrow(() -> register(body, status().isConflict()));
    }
}
