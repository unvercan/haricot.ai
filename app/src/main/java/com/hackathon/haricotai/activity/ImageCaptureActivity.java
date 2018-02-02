package com.hackathon.haricotai.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hackathon.haricotai.Commons;
import com.hackathon.haricotai.Constants;
import com.hackathon.haricotai.Credentials;
import com.hackathon.haricotai.R;
import com.hackathon.haricotai.Services;
import com.hackathon.haricotai.model.Result;
import com.hackathon.haricotai.model.database.Food;
import com.hackathon.haricotai.model.database.Ingredient;
import com.hackathon.haricotai.utility.SharedPreferences;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageCaptureActivity extends AppCompatActivity {

    private static File image;
    private static File imageDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_capture);

        // permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        this.createDirectoryForPictures();
        this.startImageCapture();
    }

    private void startImageCapture() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Commons.LAST_CAPTURED_IMAGE_FILE_NAME = generateImageName();
        image = new File(imageDirectory, Commons.LAST_CAPTURED_IMAGE_FILE_NAME);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        this.startActivityForResult(imageCaptureIntent, Constants.REQUEST_ACTIVITY_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ACTIVITY_IMAGE_CAPTURE) {
            Bitmap scaledBitmap = this.scaleImage(Constants.IMAGE_FILE_SCALE_RATIO);
            List<Result> results = null;
            if (scaledBitmap != null) {
                InputStream imageStream = convertBitmapToImage(scaledBitmap, Constants.IMAGE_FILE_FORMAT, Constants.IMAGE_FILE_QUALITY);
                ClassifiedImages result = Services.imageRecognition.classify(imageStream, Commons.LAST_CAPTURED_IMAGE_FILE_NAME, Credentials.VISUAL_RECOGNITION_API_CLASSIFIER);
                try {
                    imageStream.close();
                } catch (IOException e) {
                    Log.i(Constants.LOGGING, "image-capture-activity: : " + e.getMessage());
                }
                results = this.convertCameraResultToResults(result);
            }
            if (results != null) {
                if (results.size() > 0) {
                    List<String> filteredResults = this.filterCameraResults(results);
                    List<Food> foods = this.filterFoodsByUserAllergens(filteredResults);
                    this.goToResultActivity(foods);
                }
            }
        }
    }

    private String generateImageName() {
        long timeStamp = new Date().getTime();
        return Constants.IMAGE_FILE_PRE_NAME + "_" + timeStamp + "." + Constants.IMAGE_FILE_EXTENSION;
    }

    public InputStream convertBitmapToImage(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(format, quality, output);
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        try {
            output.close();
        } catch (IOException e) {
            Log.i(Constants.LOGGING, "image-operations: " + e.getMessage());
        }
        return input;
    }

    private Bitmap scaleImage(float ratio) {
        String imagePath = image.getAbsolutePath();
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        int newImageWidth = (int) (imageBitmap.getWidth() * ratio);
        int newImageHeight = (int) (imageBitmap.getHeight() * ratio);
        return Bitmap.createScaledBitmap(imageBitmap, newImageWidth, newImageHeight, false);
    }

    private void goToResultActivity(List<Food> foods) {
        Intent resultActivityIntent = new Intent();
        if (foods.size() > 0)
            resultActivityIntent.putExtra(Constants.RESULT_TYPE_CAMERA, foods.get(0));
        this.setResult(Activity.RESULT_OK, resultActivityIntent);
        this.finish();
    }

    private void createDirectoryForPictures() {
        imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGE_FILES_DIRECTORY_NAME);
        if (!imageDirectory.exists()) {
            boolean directoryCreated = imageDirectory.mkdirs();
            if (directoryCreated)
                Log.i(Constants.LOGGING, "image-capture-activity: images directory is created");
        }
    }

    private List<Result> convertCameraResultToResults(ClassifiedImages cameraResult) {
        List<Result> results = new ArrayList<>();
        if (cameraResult != null)
            if (cameraResult.getImages().size() > 0)
                for (ClassifierResult classifier : cameraResult.getImages().get(0).getClassifiers())
                    for (ClassResult classResult : classifier.getClasses())
                        results.add(new Result(classResult.getClassName(), classResult.getScore()));
        return results;
    }

    private List<String> filterCameraResults(List<Result> cameraResults) {
        List<String> filteredResults = new ArrayList<>();
        for (Result result : cameraResults)
            if (result.getScore() >= Constants.VISUAL_RECOGNITION_THRESHOLD)
                filteredResults.add(result.getName());
        return filteredResults;
    }

    private List<Food> filterFoodsByUserAllergens(List<String> foodNames) {
        List<Food> foods = Services.database.getFoodsByNames(foodNames);
        List<String> userAllergens = SharedPreferences.loadPreferences(this.getApplicationContext(), Constants.SHARED_PREFERENCE_NAME, Constants.SHARED_PREFERENCE_DATA_NAME);
        List<Food> detectedAllergenFoods = new ArrayList<>();
        if (foods != null) {
            for (Food food : foods) {
                for (Ingredient allergen : food.getIngredients()) {
                    if (userAllergens.contains(allergen.getId())) {
                        food.getDetectedAllergens().add(allergen);
                        if (!detectedAllergenFoods.contains(food))
                            detectedAllergenFoods.add(food);
                    }
                }
            }
        }
        return detectedAllergenFoods;
    }
}
