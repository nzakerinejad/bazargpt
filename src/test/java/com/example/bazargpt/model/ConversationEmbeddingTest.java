package com.example.bazargpt.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ConversationEmbeddingTest {

    @Test
    public void testSerialization() {
        ConversationEmbedding convEmb = new ConversationEmbedding();
        var input = List.of(0f,2f);
        convEmb.setEmbedding(input);
        var output = convEmb.getEmbedding();
        assertEquals(input, output);
    }
}