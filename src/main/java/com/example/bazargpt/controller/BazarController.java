package com.example.bazargpt.controller;

import com.example.bazargpt.model.Conversation;
import com.example.bazargpt.model.Message;
import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.ConversationEmbeddingRepository;
import com.example.bazargpt.repository.ConversationRepository;
import com.example.bazargpt.repository.MessageRepository;
import com.example.bazargpt.repository.UserRepository;
import com.example.bazargpt.service.ConversationEmbeddingDTO;
import com.example.bazargpt.service.EmbeddingService;
import com.example.bazargpt.service.OpenAIWrapper;
import com.example.bazargpt.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

record Person(String name, int age) {
}

record RegisterUserApiDTO(String email, String password, String firstName, String lastName) {
}

record LoginUserApiDTO(String email, String password) {
}

record MessageDTO(String email, String message, Long conversationId) {
}

record ResponseDTO(String userMessage, String responseMessage, Long conversationId) {
}

record ChatGetDTO(Long conversationId, ResponseDTO[] responseDTOArray) {
}

record ConversationDTO(Long conversationId, String conversationSummary) {
}

record EmbeddingDTO(String userMessage, String responseMessage) {
}

@RestController
public class BazarController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ConversationRepository conversationRep;

    @Autowired
    private MessageRepository messageRep;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationEmbeddingRepository conversationEmbeddingRepo;

    @Autowired
    private EmbeddingService embeddingService;


    @PostMapping("/register")
    public boolean register(RegisterUserApiDTO userDTO) {

        User user = new User();
        user.setPassword(userDTO.password());
        user.setEmail(userDTO.email());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());

        userService.createUser(user);
        return true;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserApiDTO loginUserDTO) {

        if (userRepo.findByEmail(loginUserDTO.email()) != null)
            return new ResponseEntity<>(OK);
        else
            return new ResponseEntity<>(UNAUTHORIZED);

    }

    @PostMapping("/chat")
    public ResponseDTO sendMessage(@RequestBody MessageDTO messageDTO) throws IOException {
        String userMessage = messageDTO.message();

        String responseMessage = userService.getResponse(userMessage);

        long conversationId;
        Conversation conversation = conversationRep.findByConversationId(messageDTO.conversationId());

        Message newMessage = new Message();
        newMessage.setContent(userMessage);
        newMessage.setResponse(responseMessage);
        if (conversation != null) {
            conversationId = messageDTO.conversationId();
            newMessage.setConversation(conversation);
            messageRep.save(newMessage);
        } else {
            Conversation newConv = conversationRep.save(new Conversation());
            conversationId = newConv.getConversationId();
            newMessage.setConversation(newConv);
            messageRep.save(newMessage);
        }

        return new ResponseDTO(userMessage, responseMessage, conversationId);
    }

    @GetMapping("/chat/{conversationId}")
    public ChatGetDTO getMessage(@PathVariable(value = "conversationId") Long conversationId) {
        var messages = messageRep.findMessagesByConversationId(conversationId);


        ResponseDTO[] responseDTOArray = messages.stream().map(m -> {
            String messageContent = m.getContent();
            String messageResponse = m.getResponse();
            ResponseDTO resDTO = new ResponseDTO(messageContent, messageResponse, conversationId);
            return resDTO;
        }).toArray(ResponseDTO[]::new);

        return new ChatGetDTO(conversationId, responseDTOArray);
    }

    @GetMapping("/conversations")
    public ConversationDTO[] getAllConversations() {
        var convs = conversationRep.findAll();
        return convs.stream().map(conv -> new ConversationDTO(conv.getConversationId(), "Summary")).toArray(ConversationDTO[]::new);
    }

    @GetMapping("/embedding/{conversationId}")
    public List<Float> getEmbeddingForConversation(@PathVariable(value = "conversationId") Long conversationId) throws IOException {

        var embedding = embeddingService.summarizeAConversation(conversationId);

        return embedding;
    }

    @GetMapping("/all_embeddings")
    public List<List<Float>> getAllEmbeddings() {
        var embeddingsList = conversationEmbeddingRepo.findAll();
        return embeddingsList.stream().map(e -> e.getEmbedding()).toList();
    }

    @GetMapping("/findNearestVector/{conversationId}")
    public ConversationEmbeddingDTO findMatching(@PathVariable(value = "conversationId") Long conversationId) {
        ConversationEmbeddingDTO match = embeddingService.findNearestVectorID(conversationId);

        return match;
    }

    @PostMapping("/greeting")
    public String greetingToTheUser(MessageDTO messageDTO) {
        return "Welcome " + messageDTO.email();
    }

}
