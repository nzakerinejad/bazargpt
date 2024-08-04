package com.example.bazargpt.service;

import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public void createUser(User user) {
        userRepo.save(user);
    }
}
