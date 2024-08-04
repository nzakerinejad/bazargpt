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
//        User user = new User();

        mockMvc.perform(post("/register").content("{\"userId\":1,\"email\":\"hassan@hassan.com\",\"password\":\"hassan\",\"firstName\":\"hassan\", \"lastName\":\"hassan@hassan.com\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(post("/login").content("{\"email\":\"hassan@hassan.com\",\"password\":\"hassan\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());


    }

    @Test
    public void testNotRegisteredUSerCanNotLogin() throws Exception {
//        User user = new User();
        mockMvc.perform(post("/login").content("{\"email\":\"ali@hassan.com\",\"password\":\"hassan\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());


    }
}
