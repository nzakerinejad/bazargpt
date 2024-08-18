package com.example.bazargpt;

import com.example.bazargpt.model.User;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
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

        mockMvc.perform(post("/chat").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")

        ).andExpect(status().isOk());
    }

    @Test
    public void testCreateNewConversationWhenUserStartsNewChat() throws Exception{

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
        ).andExpect(status().isOk())
                .andReturn();
        int convId = readConversationId(mockConversation);
        System.out.println("conversationId: " + convId);
    }

    @Test
    public void testCreatedConversionsHaveDifferentIds() throws Exception{

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "sallam ", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "mamad ", null)
                )).andExpect(status().isOk()).andReturn();
        int convId2 = readConversationId(mockConversation2);

        assertNotEquals(convId1, convId2);
    }

    @Test
    public void testFirstMessagesAreSaved() throws Exception{

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal2", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                get("/chat/{conversationId}", Integer.toString(convId1))
        ).andExpect(status().isOk()).andReturn();
        int convId2 = readConversationId(mockConversation2);
        assertEquals(convId1, convId2);

        String message = JsonPath.read(mockConversation2.getResponse().getContentAsString(), "$.messages[0]");

        assertEquals("kachal2", message);
    }

    @Test
    public void testSecondMessagesAreSaved() throws Exception{

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal1", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal2", convId1)
                )).andExpect(status().isOk()).andReturn();

        var mockConversation3 = mockMvc.perform(
                get("/chat/{conversationId}", Integer.toString(convId1))
        ).andExpect(status().isOk()).andReturn();
        int convId2 = readConversationId(mockConversation2);

        JSONArray message1 = JsonPath.read(mockConversation3.getResponse().getContentAsString(), "$.messages");
        String[] message2 = message1.toArray(String[]::new);

        assertEquals("kachal1", message2[0]);
        assertEquals("kachal2", message2[1]);
    }

    @Test
    public void testShowAllConversations() throws Exception{

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal1", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("hassan@hassan.com", "Man Hassanam",null)
                )).andExpect(status().isOk()).andReturn();
        int convId2 = readConversationId(mockConversation2);


        var response = mockMvc.perform(
                get("/conversations")
        ).andExpect(status().isOk()).andReturn();
        System.out.println("convId1: " + convId1);
        System.out.println("convId2: " + convId2);

        String responseAsString = response.getResponse().getContentAsString();

        assertTrue(responseAsString.contains(Long.toString(convId1)));
        assertTrue(responseAsString.contains(Long.toString(convId2)));

    }


    private static Integer readConversationId(MvcResult mockConversation) throws UnsupportedEncodingException {
        return JsonPath.read(mockConversation.getResponse().getContentAsString(), "$.conversationId");
    }


    private static String getContent(String email, String message, Integer id) {
        return String.format("""
                {"email":"%s", "message":"%s", "conversationId": %d}
                """, email, message, id);
    }

    @Test
    public void testGreeting() throws Exception {

        mockMvc.perform(post("/greeting").content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }
}
