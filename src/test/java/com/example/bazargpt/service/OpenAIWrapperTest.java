package com.example.bazargpt.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIWrapperTest {

    @Autowired
    OpenAIWrapper openAI;

    @Test
    public void testEmbeddingsAreDifferentForDifferentConversations() throws IOException {
        String message1 = "I have a question about moon.";
        var embeddings1 = openAI.getEmbeddingForConversationFromOpenAI(message1);

        String message2 = "Need help to find a doctor.";
        var embeddings2 = openAI.getEmbeddingForConversationFromOpenAI(message2);

        assertNotEquals(embeddings1, embeddings2);
    }

    @Test
    public void testSemanticMeaning() throws IOException {
        String ms1 = "King";
        var kingEmbedding = openAI.getEmbeddingForConversationFromOpenAI(ms1);

        String ms2 = "Queen";
        var queenEmbedding = openAI.getEmbeddingForConversationFromOpenAI(ms2);

        String ms3 = "Man";
        var manEmbedding = openAI.getEmbeddingForConversationFromOpenAI(ms3);

        String ms4 = "Woman";
        var womanEmbedding = openAI.getEmbeddingForConversationFromOpenAI(ms4);

        List<Float> result = subtractVectors(kingEmbedding, manEmbedding);
        result = addVectors(result, womanEmbedding);
        result = subtractVectors(result, queenEmbedding);

        assertNearlyZero(result);
    }

    @Test
    public void testOpenAISummarizeAConversation() throws IOException {
        ArrayList<String> ml1 = new ArrayList<>(Arrays.asList("What do you need?", "a shirt", "Which size?", "Large, for men", "which color?", "white"));
        String joinedInput = String.join(" ", ml1);
        String summary = openAI.getOpenAIResponse("Please summarize the following conversation in one sentences: " + ml1);

        System.out.println(summary);
    }

    private List<Float> subtractVectors(List<Float> vectorA, List<Float> vectorB) {
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < vectorA.size(); i++) {
            result.add(vectorA.get(i) - vectorB.get(i));
        }
        return result;
    }

    private List<Float> addVectors(List<Float> vectorA, List<Float> vectorB) {
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < vectorA.size(); i++) {
            result.add(vectorA.get(i) + vectorB.get(i));
        }
        return result;
    }

    private void assertNearlyZero(List<Float> vector) {
        float threshold = 0.09f;
        for (Float value : vector) {
            assertTrue(Math.abs(value) < threshold);
        }
    }

}