package com.hackathon.accelerator.service;

import android.util.Log;

import com.hackathon.accelerator.model.nlp.ContextData;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Constants.log;
import static com.hackathon.accelerator.utility.Credentials.CHAT_BOT_PASSWORD;
import static com.hackathon.accelerator.utility.Credentials.CHAT_BOT_USERNAME;
import static com.hackathon.accelerator.utility.Credentials.CHAT_BOT_VERSION;
import static com.hackathon.accelerator.utility.Credentials.CHAT_BOT_WORKSPACE;

public class ChatBot {

    private static ChatBot instance;
    private static Context context;
    private Conversation service;

    private ChatBot() {
        this.service = new Conversation(CHAT_BOT_VERSION, CHAT_BOT_USERNAME, CHAT_BOT_PASSWORD);
    }

    public static ChatBot getInstance() {
        if (instance == null)
            instance = new ChatBot();
        return instance;
    }


    public MessageResponse sendMessage(String inputMessage, ContextData contextData) {
        InputData.Builder input = new InputData.Builder(inputMessage);
        Log.i(log, "chat bot: input: " + inputMessage);
        MessageOptions.Builder optionsBuilder = new MessageOptions.Builder(CHAT_BOT_WORKSPACE).input(input.build());
        if (context != null) {
            if (contextData != null) {
                if (contextData.getFood() != null)
                    context.put("food", contextData.getFood());
                if (contextData.getAllIngredients() != null)
                    context.put("ingredients", contextData.getAllIngredients());
                if (contextData.getAllergenIngredients() != null)
                    context.put("allergens", contextData.getAllergenIngredients());
            }
            optionsBuilder.context(context);
        }
        MessageOptions options = optionsBuilder.build();
        return service.message(options).execute();
    }

    private List<String> getIntents(MessageResponse messageResponse) {
        List<String> intents = new ArrayList<>();
        for (RuntimeIntent intent : messageResponse.getIntents()) {
            intents.add(intent.getIntent());
            Log.i(log, "chat bot: intent: " + intent.getIntent());
        }
        return intents;
    }

    public boolean intentFound(MessageResponse messageResponse, String demandedIntent) {
        for (String intent : getIntents(messageResponse))
            if (intent.equalsIgnoreCase(demandedIntent)) {
                Log.i(log, "chat bot: demanded intent " + demandedIntent + " is found.");
                return true;
            }
        return false;
    }

    public List<String> getResponses(MessageResponse messageResponse) {
        List<String> responseMessages = messageResponse.getOutput().getText();
        for (String responseMessage : responseMessages)
            Log.i(log, "chat bot: output: " + responseMessage);
        context = messageResponse.getContext();
        return responseMessages;
    }
}