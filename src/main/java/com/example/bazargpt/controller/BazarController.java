package com.example.bazargpt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

record Person (String name, int age) {}
@RestController
public class BazarController {
    @GetMapping("/index")
    public Person mouse() {
        return new Person("hassan", 10);
    }
    @PostMapping("/mouse_entered")
    public Person mouseEntered() {
        return new Person("Ali", 20);
    }

}
