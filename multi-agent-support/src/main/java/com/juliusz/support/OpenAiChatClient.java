package com.juliusz.support;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import java.util.List;

public class OpenAiChatClient {

    private final OpenAIClient client;

    public OpenAiChatClient() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    public String sendMessage(List<String> history) {
        StringBuilder prompt = new StringBuilder();
        for (String line : history) {
            prompt.append(line).append("\n");
        }

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1)
                .addUserMessage(prompt.toString())
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().orElse("");
    }

    public String sendSingleTurnPrompt(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1)
                .addUserMessage(prompt)
                .build();
    
        ChatCompletion completion = client.chat().completions().create(params);
    
        return completion.choices().get(0).message().content().orElse("");
    }
    
}
