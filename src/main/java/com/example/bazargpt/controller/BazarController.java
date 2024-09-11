package com.example.bazargpt.controller;

import com.example.bazargpt.model.Conversation;
import com.example.bazargpt.model.ConversationEmbedding;
import com.example.bazargpt.model.Message;
import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.ConversationEmbeddingRepository;
import com.example.bazargpt.repository.ConversationRepository;
import com.example.bazargpt.repository.MessageRepository;
import com.example.bazargpt.repository.UserRepository;
import com.example.bazargpt.service.OpenAIWrapper;
import com.example.bazargpt.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

record Person (String name, int age) {}

record RegisterUserApiDTO (String email, String password, String firstName, String lastName) {}

record LoginUserApiDTO (String email, String password) {}

record MessageDTO (String email, String message, Long conversationId) {}
record ResponseDTO (String userMessage, String responseMessage, Long conversationId) {}

record ChatGetDTO(Long conversationId, ResponseDTO[] responseDTOArray) {}
record ConversationtDTO(Long conversationId, String conversationSummary) {}

record EmbeddingDTO(String userMessage, String responseMessage) {}

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
    private OpenAIWrapper openAIWrapper;

    @Autowired
    private ConversationEmbeddingRepository conversationEmbeddingRepo;

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
        System.out.println("Hassan 1 Salam");
        String responseMessage = userService.getResponse(userMessage);
        long conversationId;
        System.out.println("Hassan 222 Salam");
        Conversation conversation = conversationRep.findByConversationId(messageDTO.conversationId());

        Message newMessage = new Message();
        newMessage.setContent(userMessage);
        newMessage.setResponse(responseMessage);
        if (conversation != null) {
            conversationId = messageDTO.conversationId();
            newMessage.setConversation(conversation);
            messageRep.save(newMessage);
        }
        else {
            Conversation newConv = conversationRep.save(new Conversation());
            conversationId = newConv.getConversationId();
            newMessage.setConversation(newConv);
            messageRep.save(newMessage);
        }

        return new ResponseDTO(userMessage, responseMessage, conversationId);
    }



    @GetMapping("/chat/{conversationId}")
    public ChatGetDTO getMessage(@PathVariable(value="conversationId") Long conversationId) {
        var messages = messageRep.findMessagesByConversationId(conversationId);


        ResponseDTO[] responseDTOArray = messages.stream().map(m -> { String messageContent = m.getContent();
            String messageResponse = m.getResponse();
            ResponseDTO resDTO = new ResponseDTO(messageContent, messageResponse, conversationId);
            return resDTO;
        }).toArray(ResponseDTO[]::new);

        return new ChatGetDTO(conversationId, responseDTOArray);

    }

    @GetMapping("/conversations")
    public ConversationtDTO[] getAllConversations() {
        var convs = conversationRep.findAll();
        return convs.stream().map(conv -> new ConversationtDTO(conv.getConversationId(), "Summary")).toArray(ConversationtDTO[]::new);
    }

    @GetMapping("/embedding/{conversationId}")
    public List<Float> getEmbeddingForConversation(@PathVariable(value="conversationId") Long conversationId) {

        var messages = messageRep.findMessagesByConversationId(conversationId);

        ArrayList<String> ml = new ArrayList<>();

        for(var m : messages) {
            ml.add(m.getContent());
            ml.add(m.getResponse());
        }
        var embedding = openAIWrapper.getEmbeddingForConversationFromOpenAI(ml);

        ConversationEmbedding convEmb = new ConversationEmbedding();
        convEmb.setConversation(conversationRep.findByConversationId(conversationId));
        convEmb.setEmbedding(embedding);

        conversationEmbeddingRepo.save(convEmb);

        return embedding;
    }

    @GetMapping("/all_embeddings")
    public List<List<Float>> getAllEmbeddings() {
        var embeddingsList = conversationEmbeddingRepo.findAll();
        return embeddingsList.stream().map(e -> e.getEmbedding()).toList();
    }



    @PostMapping("/greeting")
    public String greetingToTheUser(MessageDTO messageDTO) {
        System.out.println("Hassan 333 Salam");

        return "Welcome " + messageDTO.email();

    }

}
