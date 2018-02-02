package com.hackathon.haricotai.utility;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferences {

    public static List<String> loadPreferences(Context context, String preferenceName, String preferenceDataNam) {
        List<String> userAllergens = new ArrayList<>();
        android.content.SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(preferenceName, 0);
        Set<String> userAllergensSet = preferences.getStringSet(preferenceDataNam, null);
        if (userAllergensSet != null)
            userAllergens.addAll(userAllergensSet);
        return userAllergens;
    }

    public static void savePreferences(Context context, List<String> data, String preferenceName, String preferenceDataName) {
        if (data != null) {
            Set<String> userAllergensSet = new HashSet<>();
            userAllergensSet.addAll(data);
            android.content.SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(preferenceName, 0);
            android.content.SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(preferenceDataName, userAllergensSet);
            editor.apply();
        }
    }
}