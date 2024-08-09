package com.example.bazargpt.controller;

import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.UserRepository;
import com.example.bazargpt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

record Person (String name, int age) {}

record RegisterUserApiDTO (String email, String password, String firstName, String lastName) {}

record LoginUserApiDTO (String email, String password) {}

record MessageDTO (String email, String message) {}
record ResponseDTO (String userMessage, String responseMessage) {}

@RestController
public class BazarController {

    @Autowired
    private UserRepository userRepo;

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
            return new ResponseEntity(OK);
        else
            return new ResponseEntity(UNAUTHORIZED);

    }

//    @PostMapping("/chat")
//    public ResponseDTO sendMessage(MessageDTO messageDTO) {
//        return new ResponseDTO(messageDTO.message(), "server says hello!");
//    }

    @PostMapping("/chat")
    public String sendMessage(MessageDTO messageDTO) {
        String userMessage = messageDTO.message();
        String responseMessage = "server says hello!";

        // Create the HTML fragment as a string
        return "<div class='message-line'>" +
                "<p><strong>You:</strong> " + userMessage + "</p>" +
                "<p><strong>Server:</strong> " + responseMessage + "</p>" +
                "</div>";
    }

    @PostMapping("/greeting")
    public String greetingToTheUser(MessageDTO messageDTO) {

        return "Welcome " + messageDTO.email();

    }



}
