package com.example.bazargpt.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="conversation")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToOne(mappedBy = "conversation", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ConversationEmbedding conversationEmbedding;


    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public ConversationEmbedding getConversationEmbedding() {
        return conversationEmbedding;
    }

    public void setConversationEmbedding(ConversationEmbedding conversationEmbedding) {
        this.conversationEmbedding = conversationEmbedding;
    }
}
