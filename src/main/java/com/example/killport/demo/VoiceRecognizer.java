package com.example.killport.demo;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Service
public class VoiceRecognizer {

    @Autowired
    private PortStopService portStopService;

    private static final String ACOUSTIC_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String GRAMMAR = "resource:/edu/cmu/sphinx/models/en-us";
    private static final String LANGUAGE_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";

    @PostConstruct
    public void startListening() {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY);
        configuration.setGrammarPath(GRAMMAR);
        configuration.setLanguageModelPath(LANGUAGE_MODEL);
        configuration.setGrammarName("stopport");
        configuration.setUseGrammar(true);

        LiveSpeechRecognizer recognizer = null;
        try {
            recognizer = new LiveSpeechRecognizer(configuration);
            recognizer.startRecognition(true);
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                String command = result.getHypothesis();
                System.out.println("Recognized: " + command);
                if (command.equalsIgnoreCase("stop port") || command.equalsIgnoreCase("стоп порт")) {
                    int port = 8080; // Порт по умолчанию
                    System.out.println("Stopping processes on port " + port + "...");
                    portStopService.stopProcessesOnPort(port);
                }
            }
            recognizer.stopRecognition();
        } catch (Exception e) {
            System.err.println("Error initializing speech recognizer: " + e.getMessage());
        } finally {
            if (recognizer != null) {
                recognizer.stopRecognition();
            }
        }
    }
}
