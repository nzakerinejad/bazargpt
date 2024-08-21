package com.example.bazargpt.controller;

import com.example.bazargpt.model.Conversation;
import com.example.bazargpt.model.Message;
import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.ConversationRepository;
import com.example.bazargpt.repository.MessageRepository;
import com.example.bazargpt.repository.UserRepository;
import com.example.bazargpt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

record Person (String name, int age) {}

record RegisterUserApiDTO (String email, String password, String firstName, String lastName) {}

record LoginUserApiDTO (String email, String password) {}

record MessageDTO (String email, String message, Long conversationId) {}
record ResponseDTO (String userMessage, String responseMessage, Long conversationId) {}
//record ChatGetDTO(Long conversationId, String[] messages, String[] responses) {}
record ChatGetDTO(Long conversationId, ResponseDTO[] responseDTOArray) {}
record ConversationtDTO(Long conversationId, String conversationSummary) {}
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

//    private CustomerService customerService;

//    @GetMapping("/index_old")
//    public Person mouse() {
//        return new Person("hassan", 10);
//    }
//    @PostMapping("/mouse_entered")
//    public Person mouseEntered() {
//        return new Person("Ali", 20);
//    }

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
    public ResponseDTO sendMessage(@RequestBody MessageDTO messageDTO) {
        String userMessage = messageDTO.message();
        String responseMessage = "server says hello!";
        long conversationId;

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

//        String[] messageArray = messages.stream().map(Message::getContent).toArray(String[]::new);
//        String[] responseArray = messages.stream().map(Message::getResponse).toArray(String[]::new);

        ResponseDTO[] responseDTOArray = messages.stream().map(m -> { String messageContent = m.getContent();
            String messageResponse = m.getResponse();
            ResponseDTO resDTO = new ResponseDTO(messageContent, messageResponse, conversationId);
            return resDTO;
        }).toArray(ResponseDTO[]::new);

        return new ChatGetDTO(conversationId, responseDTOArray);

//        return new ChatGetDTO(conversationId, messageArray, responseArray);
    }

    @GetMapping("/conversations")
    public ConversationtDTO[] getAllConversations() {
        var convs = conversationRep.findAll();
        return convs.stream().map(conv -> new ConversationtDTO(conv.getConversationId(), "Summary")).toArray(ConversationtDTO[]::new);
    }

        @PostMapping("/greeting")
    public String greetingToTheUser(MessageDTO messageDTO) {

        return "Welcome " + messageDTO.email();

    }



}
