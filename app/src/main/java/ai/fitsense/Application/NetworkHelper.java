package ai.fitsense.Application;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import ai.fitsense.BuildConfig;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class NetworkHelper {
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String TRANSCRIPTION_API_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String TTS_API_URL = "https://api.openai.com/v1/audio/speech";

    private OkHttpClient client;

    public NetworkHelper() {
        client = new OkHttpClient();
    }

    public void transcribeAudio(String filePath, Callback callback) {
        File audioFile = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(), fileBody)
                .addFormDataPart("model", "whisper-1")
                .addFormDataPart("response_format", "verbose_json")
                .build();

        Request request = new Request.Builder()
                .url(TRANSCRIPTION_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void generateSpeech(String text, Callback callback) {
        // Create JSON body with text and voice
        String json = new Gson().toJson(new TTSRequest(text, "tts-1", "fable"));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(TTS_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }


    private static class TTSRequest {
        private final String input;
        private final String model;
        private final String voice;

        public TTSRequest(String input, String model, String voice) {
            this.input = input;
            this.model = model;
            this.voice = voice;
        }
    }
}