package com.example.bazargpt.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="embeddings")
public class ConversationEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="embedding_id")
    private Long embeddingId;

    @Column(name="embedding_vector")
    private String embedding;

    @OneToOne
    @MapsId
    @JoinColumn(name="conversation_id")
    private Conversation conversation;

    public Long getEmbeddingId() {
        return embeddingId;
    }

    public void setEmbeddingId(long id) {
        this.embeddingId = id;
    }

    public List<Float> getEmbedding() {
        return Arrays.stream(embedding.split(",")).map(s -> Float.valueOf(s)).toList();
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding.toString().replaceAll("[\\[\\]]", "");
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

}
