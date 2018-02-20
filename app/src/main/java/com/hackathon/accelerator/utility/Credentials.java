package com.hackathon.accelerator.utility;

import android.annotation.SuppressLint;

import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

public class Credentials {

    // chat bot
    public final static String CHAT_BOT_WORKSPACE = "82defec6-9a02-4971-9c20-debdf8013786";
    public final static String CHAT_BOT_VERSION = Conversation.VERSION_DATE_2017_05_26;
    public final static String CHAT_BOT_USERNAME = "62568e16-9b43-4e5f-bab2-9698f757f4c4";
    public final static String CHAT_BOT_PASSWORD = "VtxY1JGuI5wS";

    // nlp
    public final static String NLP_USERNAME = "4c80e9d5-0c8e-49ca-b068-224acd020715";
    public final static String NLP_PASSWORD = "oUo3Zxz0Jinw";
    public final static String NLP_VERSION = NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27;

    // image classifier
    public static final String IMAGE_CLASSIFIER_API_KEY = "2ac854bf5bf677ee7928dff44f0bf49c80aa52e7";
    public static final String IMAGE_CLASSIFIER_VERSION = VisualRecognition.VERSION_DATE_2016_05_20;
    public static final String IMAGE_CLASSIFIER_CLASSIFIER_ID = "FoodClassifier_1982786710";

    // cloud database
    @SuppressLint("AuthLeak")
    public static final String CLOUD_DATABASE_CONNECTION = "https://76329ca8-1be8-4499-85b7-c9ccc9d3a982-bluemix:851ae03237f5870ff8723eeaa333452b042335b5e4bcaea1578b9607371600ba@76329ca8-1be8-4499-85b7-c9ccc9d3a982-bluemix.cloudant.com";
    public static final String CLOUD_DATABASE_NAME = "foods";
}
