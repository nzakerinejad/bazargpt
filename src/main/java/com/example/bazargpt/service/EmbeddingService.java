package com.example.bazargpt.service;

import com.example.bazargpt.model.Conversation;
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

    public Long findNearestVectorID(Long conversationId) {
        Conversation conversation = conversationRep.findByConversationId(conversationId);
        ConversationEmbedding convEmb = conversation.getConversationEmbedding();

        List<EmbeddingIdPair> embeddingsList = conversationEmbeddingRepo.findAll().stream().map(e -> new EmbeddingIdPair(e.getEmbeddingId(), e.getEmbedding())).toList();
        var match = findClosestVector(embeddingsList, new EmbeddingIdPair(convEmb.getEmbeddingId(), convEmb.getEmbedding()));
        return conversationEmbeddingRepo.findByEmbeddingId(match.embeddingId()).getConversation().getConversationId();

    }

    private EmbeddingIdPair findClosestVector(List<EmbeddingIdPair> embeddingsList, EmbeddingIdPair targetVector) {
        double minDistance = Double.MAX_VALUE;
        EmbeddingIdPair closestVector = new EmbeddingIdPair(null, null);

        for (EmbeddingIdPair vector : embeddingsList) {
            if (vector.embeddingId != targetVector.embeddingId) {
                double distance = euclideanDistance(vector.embedding(), targetVector.embedding());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestVector = vector;
                }
            }

        }

        return closestVector;
    }

    private double euclideanDistance(List<Float> vector, List<Float> targetVector) {
        double sum = 0.0f;
        for (int i = 0; i < vector.size(); i++) {
            sum += Math.pow(vector.get(i) - targetVector.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    private record EmbeddingIdPair(Long embeddingId, List<Float> embedding) {
    }
}
