package com.example.bazargpt.repository;

import com.example.bazargpt.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE c.user.userId = :userId")
    List<Conversation> findConversationsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.conversationId = :conversationId")
    public Conversation findByConversationId(@Param("conversationId") Long conversationId);

}
