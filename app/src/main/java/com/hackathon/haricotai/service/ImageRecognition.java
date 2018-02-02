package com.hackathon.haricotai.service;

import com.hackathon.haricotai.Credentials;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.InputStream;

public class ImageRecognition {

    private static ImageRecognition instance;
    private VisualRecognition service;

    private ImageRecognition() {
        this.service = new VisualRecognition(
                Credentials.VISUAL_RECOGNITION_API_VERSION,
                Credentials.VISUAL_RECOGNITION_API_KEY
        );
    }

    public static ImageRecognition getInstance() {
        if (instance == null)
            instance = new ImageRecognition();
        return instance;
    }

    public ClassifiedImages classify(InputStream ImageStream, String imageFileName, String classifierID) {
        ClassifyOptions.Builder builder = new ClassifyOptions.Builder();
        builder.imagesFile(ImageStream);
        builder.imagesFilename(imageFileName);
        builder.parameters("{\"classifier_ids\": [\"" + classifierID + "\"]}");
        ClassifyOptions options = builder.build();
        return this.service.classify(options).execute();
    }
}
