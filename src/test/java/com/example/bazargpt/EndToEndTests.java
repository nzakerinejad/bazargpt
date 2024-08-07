package com.example.bazargpt;

import com.example.bazargpt.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EndToEndTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisteredUSerCanLogin() throws Exception {
        User user = new User();

        mockMvc.perform(post("/register").param("email", "hassan@hassan.com")
                        .param("password","hassan")
                        .param("firstName","hassan")
                                .param("lastName","hassan")
        ).andExpect(status().isOk());

        mockMvc.perform(post("/login").content("{\"email\":\"hassan@hassan.com\",\"password\":\"hassan\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());


    }

    @Test
    public void testNotRegisteredUSerCanNotLogin() throws Exception {

        mockMvc.perform(post("/login").content("{\"email\":\"ali@hassan.com\",\"password\":\"hassan\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());


    }

    @Test
    public void testUserSendMessageAndRecieveResponse() throws Exception{

        mockMvc.perform(post("/chat").content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void testGreeting() throws Exception {

        mockMvc.perform(post("/greeting").content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }
}
