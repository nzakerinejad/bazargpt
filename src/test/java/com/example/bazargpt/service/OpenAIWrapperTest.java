package com.example.bazargpt.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIWrapperTest {

    @Autowired
    OpenAIWrapper openAI;

    @Test
    public void testOpenAIWrapper() throws IOException {
        ArrayList<String> ml = new ArrayList<>(Arrays.asList("hi. how are you", "ok"));
        var embeddings = openAI.getEmbeddingForConversationFromOpenAI(ml);
        assertTrue(embeddings.size()>10);
    }

}