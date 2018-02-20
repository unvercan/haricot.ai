package com.hackathon.accelerator.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.hackathon.accelerator.R;
import com.hackathon.accelerator.model.ClassWithScore;
import com.hackathon.accelerator.model.database.Food;
import com.hackathon.accelerator.model.database.Ingredient;
import com.hackathon.accelerator.service.CloudDatabase;
import com.hackathon.accelerator.service.ImageClassifier;
import com.hackathon.accelerator.utility.SharedPreferencesOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_RESULT_DATA;
import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_THRESHOLD;
import static com.hackathon.accelerator.utility.Constants.REQUEST_ACTIVITY_CHAT_BOT;
import static com.hackathon.accelerator.utility.Constants.REQUEST_ACTIVITY_IMAGE_CAPTURE;
import static com.hackathon.accelerator.utility.Constants.REQUEST_ACTIVITY_RESULT;
import static com.hackathon.accelerator.utility.Constants.imageFile;
import static com.hackathon.accelerator.utility.Constants.imageFileName;
import static com.hackathon.accelerator.utility.Constants.imagesDirectory;
import static com.hackathon.accelerator.utility.Constants.log;

public class ImageCaptureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        if (!imagesDirectory.exists()) {
            boolean directoriesCreated = imagesDirectory.mkdirs();
            if (directoriesCreated)
                Log.i(log, "image capture activity: images directory is created.");
        } else
            Log.i(log, "image capture activity: images directory exists already.");
        if (imageFile.exists()) {
            boolean lastImageDeleted = imageFile.delete();
            if (lastImageDeleted)
                Log.i(log, "image capture activity: last image is deleted.");
        } else
            Log.i(log, "image capture activity: last image doesn't exist.");
        imageFile = new File(imagesDirectory, imageFileName);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(cameraIntent, REQUEST_ACTIVITY_IMAGE_CAPTURE);
        Log.i(log, "image capture activity: camera is started.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACTIVITY_IMAGE_CAPTURE: {
                Log.i(log, "image capture activity: camera is finished.");
                ClassWithScore chosenResult = null;
                if (imageFile.exists()) {
                    Log.i(log, "image capture activity: image is taken.");
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    Bitmap scaledImage = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth() / 5, imageBitmap.getHeight() / 5, false);
                    Log.i(log, "image capture activity: image is scaled.");
                    List<ClassWithScore> classWithScores = ImageClassifier.getInstance().classify(scaledImage);
                    if (classWithScores.isEmpty()) {
                        Intent resultIntent = new Intent(this, ResultActivity.class);
                        startActivity(resultIntent);
                        finish();
                        Log.i(log, "image capture activity: result activity is started.");
                        return;
                    } else {
                        List<ClassWithScore> filteredResults = new ArrayList<>();
                        for (ClassWithScore classWithScore : classWithScores)
                            if (classWithScore.getScore() >= IMAGE_CLASSIFIER_THRESHOLD)
                                filteredResults.add(classWithScore);
                        Log.i(log, "image capture activity: results are filtered.");
                        for (ClassWithScore result : filteredResults) {
                            Log.i(log, "image capture activity: result: " + result.toString());
                            if (chosenResult == null)
                                chosenResult = result;
                            else if (result.getScore() > chosenResult.getScore())
                                chosenResult = result;
                        }
                        if (chosenResult != null)
                            Log.i(log, "image capture activity: chosen result: " + chosenResult.toString());
                    }
                } else
                    Log.i(log, "image capture activity: image can't be taken.");

                if (imageFile.exists() && chosenResult != null) {
                    Food foundedFood = CloudDatabase.getInstance().getFood(chosenResult.getName());
                    if (foundedFood != null) {
                        List<Ingredient> allergenIngredients = new ArrayList<>();
                        List<Ingredient> ingredients = foundedFood.getIngredient();
                        List<String> userAllergens = SharedPreferencesOperations.getUserAllergens(this);
                        List<String> userCrossAllergens = CloudDatabase.getInstance().getCrossAllergensByAllergens(userAllergens);
                        for (Ingredient ingredient : ingredients)
                            for (String userCrossAllergen : userCrossAllergens)
                                if (ingredient.getId().equalsIgnoreCase(userCrossAllergen)) {
                                    allergenIngredients.add(ingredient);
                                    Log.i(log, "image capture activity: allergen " + userCrossAllergen + " is matched.");
                                }
                        foundedFood.setDetectedAllergens(allergenIngredients);
                        Intent resultIntent = new Intent(this, ResultActivity.class);
                        resultIntent.putExtra(IMAGE_CLASSIFIER_RESULT_DATA, foundedFood);
                        startActivity(resultIntent);
                        finish();
                        Log.i(log, "image capture activity: result activity is started.");
                        return;
                    }
                } else {
                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    startActivity(chatIntent);
                    finish();
                    Log.i(log, "image capture activity: chat activity is started.");
                    return;
                }
            }
            case REQUEST_ACTIVITY_RESULT:
            case REQUEST_ACTIVITY_CHAT_BOT:
                finish();
        }
    }
}