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
@RestController
public class BazarController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;
//    private CustomerService customerService;

    @GetMapping("/index")
    public Person mouse() {
        return new Person("hassan", 10);
    }
    @PostMapping("/mouse_entered")
    public Person mouseEntered() {
        return new Person("Ali", 20);
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user) {
        userService.createUser(user);
        return true;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()) != null)
            return new ResponseEntity(OK);
        else
            return new ResponseEntity(UNAUTHORIZED);

    }


}
