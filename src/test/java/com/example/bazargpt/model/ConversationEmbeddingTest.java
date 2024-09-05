package com.example.bazargpt.model;

import org.junit.jupiter.api.Test;

import java.util.List;

class ConversationEmbeddingTest {

    @Test
    public void testSerialization() {
        ConversationEmbedding convEmb = new ConversationEmbedding();
        convEmb.setEmbedding(List.of(0f,2f));
        convEmb.getEmbedding();
    }
}