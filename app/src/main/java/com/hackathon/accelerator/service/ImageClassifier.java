package com.hackathon.accelerator.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.hackathon.accelerator.model.ClassWithScore;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Constants.imageFileName;
import static com.hackathon.accelerator.utility.Constants.log;
import static com.hackathon.accelerator.utility.Credentials.IMAGE_CLASSIFIER_API_KEY;
import static com.hackathon.accelerator.utility.Credentials.IMAGE_CLASSIFIER_CLASSIFIER_ID;
import static com.hackathon.accelerator.utility.Credentials.IMAGE_CLASSIFIER_VERSION;

public class ImageClassifier {

    private static ImageClassifier instance;
    private VisualRecognition service;

    private ImageClassifier() {
        this.service = new VisualRecognition(IMAGE_CLASSIFIER_VERSION, IMAGE_CLASSIFIER_API_KEY);
    }

    public static ImageClassifier getInstance() {
        if (instance == null)
            instance = new ImageClassifier();
        return instance;
    }

    public List<ClassWithScore> classify(Bitmap image) {
        List<ClassWithScore> classWithScores = null;
        if (image != null) {
            InputStream inputStream = convertBitmapToStream(image);
            Log.i(log, "image classifier: bitmap converted to stream");
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(inputStream)
                    .imagesFilename(imageFileName)
                    .parameters("{\"classifier_ids\": [\"" + IMAGE_CLASSIFIER_CLASSIFIER_ID + "\"],\"threshold\": 0.01}")
                    .build();
            ClassifiedImages result = null;
            try {
                result = service.classify(classifyOptions).execute();
                Log.i(log, "image classifier: image is classified.");
            } catch (Exception ignored) {
            }
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }

            classWithScores = analyzeClassifyResult(result);
            Log.i(log, "image classifier: image classify result is analyzed.");
        }
        return classWithScores;
    }

    private List<ClassWithScore> analyzeClassifyResult(ClassifiedImages classifiedImages) {
        List<ClassWithScore> classWithScores = new ArrayList<>();
        if (classifiedImages != null)
            if (classifiedImages.getImages().size() > 0)
                for (ClassifierResult classifierResult : classifiedImages.getImages().get(0).getClassifiers()) {
                    List<ClassResult> imageClasses = classifierResult.getClasses();
                    if (imageClasses.isEmpty())
                        Log.i(log, "image classifier: image class not found.");
                    else
                        for (ClassResult imageClass : imageClasses) {
                            ClassWithScore classWithScore = new ClassWithScore(imageClass.getClassName(), imageClass.getScore());
                            Log.i(log, "image classifier: image class: " + classWithScore.getName() + ", " + classWithScore.getScore());
                            classWithScores.add(classWithScore);
                        }
                }
        return classWithScores;
    }

    private InputStream convertBitmapToStream(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
        Log.i(log, "image classifier: image is compressed as jpeg.");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }
        return inputStream;
    }
}
