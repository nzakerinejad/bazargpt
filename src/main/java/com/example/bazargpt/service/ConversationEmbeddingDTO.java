package com.example.bazargpt.service;

import java.util.List;

public class ConversationEmbeddingDTO {

    private Long embeddingId;
    private List<Float> embeddingVector;

    public ConversationEmbeddingDTO(Long id, List<Float> embedding) {
        this.embeddingId = id;
        this.embeddingVector = embedding;
    }

    public Long getEmbeddingId() {
        return embeddingId;
    }

    public void setEmbeddingId(Long embeddingId) {
        this.embeddingId = embeddingId;
    }

    public List<Float> getEmbeddingVector() {
        return embeddingVector;
    }

    public void setEmbeddingVector(List<Float> embeddingVector) {
        this.embeddingVector = embeddingVector;
    }

}
