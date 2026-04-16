package com.academiaverse.Auth;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class TestRegister extends TestAuth{

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testRegisterEndpoint() throws Exception {
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
    }

    @Test
    void testRegisterWithoutEmail() throws Exception {
        String body = """
            {
                "firstname": "",
                "lastname": "",
                "email": "",
                "password": "test"
            }
            """;

        register(body, status().isBadRequest());
    }

    @Test
    void testRegisterWithoutPassword() throws Exception {
        String body = """
            {
                "firstname": "",
                "lastname": "",
                "email": "test@test.com",
                "password": ""
            """;

        register(body, status().isBadRequest());
    }

    @Test
    void testRegisterWithExistingEmail() throws Exception {
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
        register(body, status().isConflict());
    }
}