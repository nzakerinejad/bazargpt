package com.example.bazargpt.service;

import com.jayway.jsonpath.JsonPath;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAIWrapper {

    @Value("${openaikey}")
    private String openaikey;

    public OpenAIWrapper(String openaikey) {
        this.openaikey = openaikey;
    }

    public String getOpenAIResponse(String userMessage) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"model\": \"gpt-3.5-turbo\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n   \"content\":\"" + userMessage + "\"\n    }\n  ]\n}");
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + openaikey)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        String content = JsonPath.read(responseBody, "$.choices[0].message.content");
        return content;
    }

    public List<Float> getEmbeddingForConversationFromOpenAI(ArrayList<String> messageList) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"input\": \"Your text string goes here\",\n    \"model\": \"text-embedding-3-small\"\n}");

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/embeddings")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + openaikey)
                .build();

        List<Float> embeddingList = new ArrayList<>();

        try {
            // Send the request and get the response
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Use JsonPath to extract the embedding array
            embeddingList = JsonPath.read(responseBody, "$.data[0].embedding");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return embeddingList;

    }

}
