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
    @Value("${openaikey}")
    private String openaikey;

    public void createUser(User user) {

        userRepo.save(user);
    }

    public String getResponse(String userMessage) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"model\": \"gpt-3.5-turbo\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n   \"content\":\"" + userMessage + "\"\n    }\n  ]\n}");
        System.out.println("env var: " + openaikey);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + openaikey)
                .build();
        Response response = client.newCall(request).execute();

//        return response.body().string();
        String responseBody = response.body().string();

        // Use JsonPath to extract the "name" field from the "data" object
//        String content = JsonPath.read(responseBody, "$.data.content");
        System.out.println("Content: " + responseBody);
        String content = JsonPath.read(responseBody, "$.choices[0].message.content");
        System.out.println("Content: " + content);
//        return responseBody;
        return content;
    }
}
