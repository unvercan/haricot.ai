package com.hackathon.accelerator.utility;

import android.os.Environment;

import com.hackathon.accelerator.model.chatbot.User;

import java.io.File;

public class Constants {

    // request
    public static final int REQUEST_READ_CONTACTS = 100;
    public static final int REQUEST_ACTIVITY_RESULT = 1001;
    public static final int REQUEST_ACTIVITY_IMAGE_CAPTURE = 1002;
    public static final int REQUEST_ACTIVITY_CHAT_BOT = 1003;

    // threshold
    public static final float VISUAL_RECOGNITION_THRESHOLD = 0.9f;
    public static final float NATURAL_LANGUAGE_UNDERSTANDING_THRESHOLD = 0.01f;

    // log
    public static final String log = "HaricotAI";

    // image file
    public static final String imageFileName = "haricot_ai.jpg";
    // image classifier
    public static final String IMAGE_CLASSIFIER_RESULT_DATA = "imageClassifierResult";
    public static final String IMAGE_CLASSIFIER_FOOD_FOUND_RESULT_LABEL = "Food:";
    public static final String IMAGE_CLASSIFIER_FOOD_NOT_FOUND_RESULT_LABEL = "Food is undefined";
    public static final String IMAGE_CLASSIFIER_ALLERGIC_RESULT_DETAIL_LABEL = "Allergens";
    public static final String IMAGE_CLASSIFIER_NON_ALLERGIC_RESULT_DETAIL_LABEL = "Bon Appetite :)";
    // chat users
    public static final User user = new User("user", "User");
    public static final User bot = new User("bot", "Bot");
    // chat bot
    public static final String CHAT_BOT_GOODBYE_INTENT = "goodbye";
    public static final String CHAT_BOT_FOOD_INTENT = "food";
    public static final String CHAT_BOT_INITIAL_MESSAGE = "hi";
    // nlp
    public static final String NLP_FOOD_CATEGORY = "food";
    public static final String NLP_DRINK_CATEGORY = "food";
    // shared preferences
    static final String PREFERENCES_NAME = "UserData";
    static final String PREFERENCES_DATA = "allergens";
    private static final String imagesDirectoryName = "HaricotAI";
    public static File imagesDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imagesDirectoryName);
    public static File imageFile = new File(imagesDirectory, imageFileName);
}