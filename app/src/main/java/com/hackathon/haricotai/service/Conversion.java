package com.hackathon.haricotai.service;

import com.hackathon.haricotai.Credentials;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Conversion {
    private static Conversion instance;
    private static Context context;
    private Conversation service;

    private Conversion() {
        this.service = new Conversation(
                Credentials.CONVERSATION_API_VERSION,
                Credentials.CONVERSATION_API_USERNAME,
                Credentials.CONVERSATION_API_PASSWORD
        );
    }

    public static Conversion getInstance() {
        if (instance == null)
            instance = new Conversion();
        return instance;
    }

    public MessageResponse sendMessage(String message, List<Map.Entry<String, String[]>> metaData) {
        MessageOptions options = this.generateOptions(message, metaData);
        return this.service.message(options).execute();
    }

    public boolean checkIntentExist(MessageResponse response, String demandedIntent) {
        List<RuntimeIntent> intents = response.getIntents();
        for (RuntimeIntent runtimeIntent : intents) {
            String intent = runtimeIntent.getIntent();
            if (intent.equalsIgnoreCase(demandedIntent))
                return true;
        }
        return false;
    }

    public List<String> receiveResponses(MessageResponse messageResponse) {
        this.getContextIfNotExist(messageResponse);
        List<String> responses = new ArrayList<>();
        List<String> responseMessages = messageResponse.getOutput().getText();
        responses.addAll(responseMessages);
        return responses;
    }

    private void getContextIfNotExist(MessageResponse messageResponse) {
        if (messageResponse != null && context == null)
            context = messageResponse.getContext();
    }

    private MessageOptions generateOptions(String message, List<Map.Entry<String, String[]>> metaData) {
        InputData.Builder input = new InputData.Builder(message);
        MessageOptions.Builder optionsBuilder = new MessageOptions.Builder(Credentials.CONVERSATION_API_WORKSPACE).input(input.build());
        if (context != null && metaData != null) {
            for (AbstractMap.Entry<String, String[]> data : metaData) {
                String key = data.getKey();
                String[] value = data.getValue();
                context.put(key, value);
            }
        }
        if (context != null)
            optionsBuilder = optionsBuilder.context(context);
        return optionsBuilder.build();
    }
}
