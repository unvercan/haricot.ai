package com.hackathon.haricotai;

import android.graphics.Bitmap;

import com.hackathon.haricotai.model.conversion.User;

public class Constants {
    // chat activity
    public static final String APP_USER_ID = "user";
    public static final String BOT_USER_ID = "watson";
    // result type
    public static final String RESULT_TYPE_CAMERA = "CameraResult";
    public static final String RESULT_TYPE_CHAT = "CameraResult";
    // image file
    public static final String IMAGE_FILE_PRE_NAME = "food";
    public static final String IMAGE_FILE_EXTENSION = "JPG";
    public static final Bitmap.CompressFormat IMAGE_FILE_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int IMAGE_FILE_QUALITY = 90;
    public static final float IMAGE_FILE_SCALE_RATIO = 0.5f;
    public static final String IMAGE_FILES_DIRECTORY_NAME = "HaricotAI";
    // logging
    public static final String LOGGING = "HaricotAI";
    // shared preferences
    public static final String SHARED_PREFERENCE_NAME = "UserProfile";
    public static final String SHARED_PREFERENCE_DATA_NAME = "AllergenSet";
    // threshold
    public static final float VISUAL_RECOGNITION_THRESHOLD = 0.7f;
    public static final float NATURAL_LANGUAGE_UNDERSTANDING_THRESHOLD = 0.7f;
    // intent request codes
    public static final int REQUEST_ACTIVITY_RESULT = 1001;
    public static final int REQUEST_ACTIVITY_IMAGE_CAPTURE = 1002;
    public static final int REQUEST_ACTIVITY_CHAT = 1003;
    // permission request code
    public static final int REQUEST_PERMISSIONS_CAMERA = 100;
    // NLP demanded categories
    public static final String[] NLP_DEMANDED_CATEGORIES = new String[]{"food", "drink"};
    // chat bot
    public static final User APP_USER = new User(APP_USER_ID, "User");
    public static final User BOT_USER = new User(BOT_USER_ID, "Bot");
    // result
    public static final String RESULT_OK = "Allergens not Found!";
}
