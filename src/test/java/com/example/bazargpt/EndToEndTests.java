package com.example.bazargpt;

import com.example.bazargpt.model.Conversation;
import com.example.bazargpt.model.ConversationEmbedding;
import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.ConversationEmbeddingRepository;
import com.example.bazargpt.repository.ConversationRepository;
import com.example.bazargpt.service.OpenAIWrapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {OpenAIWrapper.class})
public class EndToEndTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAIWrapper openAIWrapperMock;

    @MockBean
    private ConversationRepository conversationRep;

    @MockBean
    private ConversationEmbeddingRepository conversationEmbeddingRepo;

    @Test
    public void testRegisteredUSerCanLogin() throws Exception {
        User user = new User();

        mockMvc.perform(post("/register").param("email", "hassan@hassan.com")
                .param("password", "hassan")
                .param("firstName", "hassan")
                .param("lastName", "hassan")
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
    public void testUserSendMessageAndRecieveResponse() throws Exception {

        mockMvc.perform(post("/chat").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")

        ).andExpect(status().isOk());
    }

    @Test
    public void testCreateNewConversationWhenUserStartsNewChat() throws Exception {

        var mockConversation = mockMvc.perform(
                        post("/chat").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
                ).andExpect(status().isOk())
                .andReturn();
        int convId = readConversationId(mockConversation);
        System.out.println("conversationId: " + convId);
    }

    @Test
    public void testCreatedConversionsHaveDifferentIds() throws Exception {

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
    public void testFirstMessagesAreSaved() throws Exception {

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal2", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                get("/chat/{conversationId}", Integer.toString(convId1))
        ).andExpect(status().isOk()).andReturn();
        int convId2 = readConversationId(mockConversation2);
        assertEquals(convId1, convId2);

        String message = JsonPath.read(mockConversation2.getResponse().getContentAsString(), "$.responseDTOArray[0].userMessage");

        assertEquals("kachal2", message);
    }

    @Test
    public void testSecondMessagesAreSaved() throws Exception {

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

        String message1 = JsonPath.read(mockConversation3.getResponse().getContentAsString(), "$.responseDTOArray[0].userMessage");
        String message2 = JsonPath.read(mockConversation3.getResponse().getContentAsString(), "$.responseDTOArray[1].userMessage");


        assertEquals("kachal1", message1);
        assertEquals("kachal2", message2);
    }

    @Test
    public void testShowAllConversations() throws Exception {

        var mockConversation = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("ali@hassan.com", "kachal1", null)
                )).andExpect(status().isOk()).andReturn();
        int convId1 = readConversationId(mockConversation);

        var mockConversation2 = mockMvc.perform(
                post("/chat").contentType(MediaType.APPLICATION_JSON).content(getContent("hassan@hassan.com", "Man Hassanam", null)
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

    @Test
    public void testGetEmbeddingForAConversation() throws Exception {
        // Ensure that mock behavior is defined
        when(openAIWrapperMock.getOpenAIResponse(anyString())).thenReturn("salaam hassan");
        when(openAIWrapperMock.getEmbeddingForConversationFromOpenAI(any())).thenReturn(List.of(0f, 1f, 2f, 3f));

        var mockConversation = mockMvc.perform(
                        post("/chat").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"ali@hassan.com\", \"message\":\"Hi, I have a question.\"}")
                ).andExpect(status().isOk())
                .andReturn();
        int convId = readConversationId(mockConversation);
        System.out.println("conversationId: " + convId);

        var responseFromChatgpt = mockMvc.perform(get("/embedding/{conversationId}", Integer.toString(convId))
        ).andExpect(status().isOk()).andReturn();

        List<Float> message1 = JsonPath.read(responseFromChatgpt.getResponse().getContentAsString(), "$");
        List<Double> expected = List.of(0d, 1d, 2d, 3d);
        assertArrayEquals(expected.toArray(), message1.toArray());

        var allEmbeddingsResponse = mockMvc.perform(get("/all_embeddings")).andExpect(status().isOk()).andReturn();
        List<List<Float>> allEmbeddingsList = JsonPath.read(allEmbeddingsResponse.getResponse().getContentAsString(), "$");
        assertTrue(allEmbeddingsList.size() >= 1);
        assertEquals(expected, allEmbeddingsList.get(0));

    }

    @Test
    public void testFindNearestItemToARequest() throws Exception {
        long conversationId = 1L;
        List<Float> vector1 = List.of(1f, 4f, 3f);
        List<Float> vector2 = List.of(0f, 5f, 7f);

        ConversationEmbedding convEmb = new ConversationEmbedding();
        convEmb.setEmbedding(vector1);

        Conversation conversation = new Conversation();
        conversation.setConversationEmbedding(convEmb);
        conversation.setConversationId(conversationId);

        when(conversationRep.findByConversationId(conversationId)).thenReturn(conversation);

        ConversationEmbedding embed1 = new ConversationEmbedding();
        embed1.setEmbeddingId(1);
        embed1.setEmbedding(vector1);

        ConversationEmbedding embed2 = new ConversationEmbedding();
        embed2.setEmbeddingId(2);
        embed2.setEmbedding(vector2);

        when(conversationEmbeddingRepo.findAll()).thenReturn(List.of(embed1, embed2));

        var nearestVector = mockMvc.perform(get("/findNearestVector/{conversationId}", Long.toString(conversationId)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the JSON response to get the vector result
        String jsonResponse = nearestVector.getResponse().getContentAsString();

        var result = JsonPath.read(jsonResponse, "$.embeddingVector");

        String expectedString = vector1.toString().replaceAll("\\s+", "");
        String resultString = result.toString().replaceAll("\\s+", "");
        assertEquals(expectedString, resultString, "The nearest vector should match the expected vector");

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
