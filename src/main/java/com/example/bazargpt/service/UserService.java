package com.example.bazargpt.service;

import com.example.bazargpt.model.User;
import com.example.bazargpt.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OpenAIWrapper openAIWrapper;

    public void createUser(User user) {

        userRepo.save(user);
    }

    public String getResponse(String userMessage) throws IOException {
        return openAIWrapper.getOpenAIResponse(userMessage);
    }

}
