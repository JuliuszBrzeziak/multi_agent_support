package com.juliusz.support;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class App {

    public static void main(String[] args) {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1)
                .addUserMessage("Say hello to me in one short sentence.")
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        completion.choices().forEach(choice ->
                choice.message().content().ifPresent(System.out::println)
        );
    }
}
