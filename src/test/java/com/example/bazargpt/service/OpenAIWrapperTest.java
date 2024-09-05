package com.example.bazargpt.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OpenAIWrapperTest {

    @Test
    public void testOpenAIWrapper() throws IOException {
        var openAI = new OpenAIWrapper(System.getenv("OPENAI_API_KEY"));
        ArrayList<String> ml = new ArrayList<>(Arrays.asList("hi. how are you", "ok"));
        var embeddings = openAI.getEmbeddingForConversationFromOpenAI(ml);
        assertTrue(embeddings.size()>10);
    }

}