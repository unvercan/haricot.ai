package com.hackathon.haricotai;

import com.hackathon.haricotai.service.CloudNoSQLDatabase;
import com.hackathon.haricotai.service.Conversion;
import com.hackathon.haricotai.service.ImageRecognition;
import com.hackathon.haricotai.service.NaturalLanguageProcessor;

public class Services {
    // services
    public static Conversion conversion = Conversion.getInstance();
    public static NaturalLanguageProcessor nlp = NaturalLanguageProcessor.getInstance();
    public static CloudNoSQLDatabase database = CloudNoSQLDatabase.getInstance();
    public static ImageRecognition imageRecognition = ImageRecognition.getInstance();
}
