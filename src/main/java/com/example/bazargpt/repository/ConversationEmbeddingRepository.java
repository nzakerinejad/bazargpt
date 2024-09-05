package com.example.bazargpt.repository;

import com.example.bazargpt.model.ConversationEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationEmbeddingRepository extends JpaRepository<ConversationEmbedding, Long> {
    @Query("SELECT c FROM ConversationEmbedding c WHERE c.embeddingId = :embeddingId")
    public ConversationEmbedding findByEmbeddingId(@Param("embeddingId") Long embeddingId);
}
