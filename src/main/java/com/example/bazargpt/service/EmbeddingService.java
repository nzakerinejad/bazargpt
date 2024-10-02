package com.example.bazargpt.service;

import com.example.bazargpt.model.ConversationEmbedding;
import com.example.bazargpt.repository.ConversationEmbeddingRepository;
import com.example.bazargpt.repository.ConversationRepository;
import com.example.bazargpt.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    @Autowired
    MessageRepository messageRep;

    @Autowired
    UserService userService;

    @Autowired
    private OpenAIWrapper openAIWrapper;

    @Autowired
    private ConversationRepository conversationRep;

    @Autowired
    private ConversationEmbeddingRepository conversationEmbeddingRepo;

    public List<Float> summarizeAConversation(Long conversationId) throws IOException {
        var messages = messageRep.findMessagesByConversationId(conversationId);

        ArrayList<String> ml = new ArrayList<>();

        for (var m : messages) {
            ml.add(m.getContent());
            ml.add(m.getResponse());
        }

        String joinedInput = String.join(" ", ml);
        String summary = userService.getResponse("Please summarize the following conversation: " + joinedInput);
        var embedding = openAIWrapper.getEmbeddingForConversationFromOpenAI(summary);

        ConversationEmbedding convEmb = new ConversationEmbedding();
        convEmb.setConversation(conversationRep.findByConversationId(conversationId));
        convEmb.setEmbedding(embedding);
        conversationEmbeddingRepo.save(convEmb);

        return embedding;
    }
}
