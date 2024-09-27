package ai.fitsense;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ai.fitsense.Application.AudioHelper;
import ai.fitsense.Application.NetworkHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextView welcomeText, exerciseDescriptionText;
    MaterialCardView workoutCard, yogaCard, meditationCard, physicalExercisesCard;
    ExtendedFloatingActionButton continueButton;
    boolean isVisible = false;
    String exerciseType = "";
    JSONArray jsonArray = new JSONArray();
    private AudioHelper audioHelper;
    ImageView closeButton;
    private NetworkHelper networkHelper;
    private MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;
    private Handler handler = new Handler();
    private Runnable checkAudioLevelRunnable;
    ImageView voiceButton;
    private static final int AUDIO_LEVEL_THRESHOLD = 4000; // Adjust based on testing
    private static final int SILENCE_DURATION_THRESHOLD = 1500; // 1.5 seconds
    private long silenceStartTime = -1;
    ImageView fitSense;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .build();
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        preferences.edit().putString("name", "Jayant").apply();
        String name = preferences.getString("name", "Jayant");
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("role", "system");
            jsonObject1.put("content", "You are an AI personal trainer named FitSense.AI pronounced as Fit Sense, created by Team Zeta consisting of 4 members Jayant, Ashwani, Yashi and Anagha don't tell about your developers until asked. Keep you answers crisp and brief to make the conversation more fluent detail only when asked to detail. Your primary role is to assist users with their workouts by detecting body points and providing guidance on proper exercise form and posture. Although the system is still in development, you can offer valuable advice on various exercises, helping users to perform them correctly and effectively. Additionally, you can provide GIF images that demonstrate exercises step-by-step, allowing users to visualise and understand how to perform each exercise correctly. If asked about your capabilities, explain that you can detect body points, guide users through exercises, and offer visual aids via GIFs to help them with their workout routines. Emphasise that while the system is under development, you are ready to provide useful exercise recommendations, corrections, and visual guidance. Keep in mind that whenever you tell about yourself always mention the problems of not having a personal trainer. Always answer in english. Also remember the users name is "+name);
            jsonArray.put(jsonObject1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        closeButton = findViewById(R.id.close_button);
        voiceButton = findViewById(R.id.voice_button);
        fitSense = findViewById(R.id.fit_sanse);
        networkHelper = new NetworkHelper();
        textToSpeech = new TextToSpeech(this, this);
        mediaPlayer = new MediaPlayer();
        audioHelper = new AudioHelper();
        welcomeText = findViewById(R.id.welcome_text);
        exerciseDescriptionText = findViewById(R.id.exercise_description_text);
        workoutCard = findViewById(R.id.workout_card);
        yogaCard = findViewById(R.id.yoga_card);
        meditationCard = findViewById(R.id.meditation_card);
        physicalExercisesCard = findViewById(R.id.physical_exercise_card);
        continueButton = findViewById(R.id.continue_button);
        workoutCard.setOnClickListener(v -> {
            if (!workoutCard.isChecked()) {
                exerciseType = "Workout";
                exerciseDescriptionText.setText("Gym workouts are an excellent way to build muscle, improve cardiovascular health, and enhance overall fitness. Regular gym sessions can help in weight management, increase strength and endurance, and boost mental well-being by reducing stress and anxiety.");
                isVisible = true;
                workoutCard.setChecked(true);
                yogaCard.setChecked(false);
                meditationCard.setChecked(false);
                physicalExercisesCard.setChecked(false);
                workoutCard.setStrokeColor(getColor(R.color.card_stroke_color));
                workoutCard.setCheckedIcon(ContextCompat.getDrawable(this, R.drawable.blank));
                continueButton.setVisibility(ExtendedFloatingActionButton.VISIBLE);
                yogaCard.setStrokeColor(getColor(R.color.stroke_color));
                meditationCard.setStrokeColor(getColor(R.color.stroke_color));
                physicalExercisesCard.setStrokeColor(getColor(R.color.stroke_color));
            }
        });
        yogaCard.setOnClickListener(v -> {
            if (!yogaCard.isChecked()) {
                exerciseType = "Yoga";
                exerciseDescriptionText.setText("Yoga offers physical and mental health benefits for people of all ages. And, if you’re going through an illness, recovering from surgery or living with a chronic condition, yoga can become an integral part of your treatment and potentially fasten healing. ");
                isVisible = true;
                workoutCard.setChecked(false);
                yogaCard.setChecked(true);
                meditationCard.setChecked(false);
                physicalExercisesCard.setChecked(false);
                workoutCard.setStrokeColor(getColor(R.color.stroke_color));
                yogaCard.setStrokeColor(getColor(R.color.card_stroke_color));
                yogaCard.setCheckedIcon(ContextCompat.getDrawable(this, R.drawable.blank));
                continueButton.setVisibility(ExtendedFloatingActionButton.VISIBLE);
                meditationCard.setStrokeColor(getColor(R.color.stroke_color));
                physicalExercisesCard.setStrokeColor(getColor(R.color.stroke_color));
            }
        });
        meditationCard.setOnClickListener(v -> {
            if (!meditationCard.isChecked()) {
                exerciseType = "Meditation";
                exerciseDescriptionText.setText("Meditation is a practice that helps in achieving mental clarity, emotional calmness, and stability. It can reduce stress, improve concentration, and promote a sense of inner peace. Regular meditation can also enhance self-awareness and overall mental health.");
                isVisible = true;
                workoutCard.setChecked(false);
                yogaCard.setChecked(false);
                meditationCard.setChecked(true);
                physicalExercisesCard.setChecked(false);
                workoutCard.setStrokeColor(getColor(R.color.stroke_color));
                yogaCard.setStrokeColor(getColor(R.color.stroke_color));
                meditationCard.setStrokeColor(getColor(R.color.card_stroke_color));
                meditationCard.setCheckedIcon(ContextCompat.getDrawable(this, R.drawable.blank));
                continueButton.setVisibility(ExtendedFloatingActionButton.VISIBLE);
                physicalExercisesCard.setStrokeColor(getColor(R.color.stroke_color));
            }
        });
        physicalExercisesCard.setOnClickListener(v -> {
            if (!physicalExercisesCard.isChecked()) {
                exerciseType = "Physical Exercise";
                exerciseDescriptionText.setText("Physical exercise is essential for maintaining a healthy body and mind. It helps in improving cardiovascular health, strengthening muscles, and enhancing flexibility. Regular physical activity can also reduce the risk of chronic diseases, improve mood, and boost energy levels.");
                isVisible = true;
                workoutCard.setChecked(false);
                yogaCard.setChecked(false);
                meditationCard.setChecked(false);
                physicalExercisesCard.setChecked(true);
                workoutCard.setStrokeColor(getColor(R.color.stroke_color));
                yogaCard.setStrokeColor(getColor(R.color.stroke_color));
                meditationCard.setStrokeColor(getColor(R.color.stroke_color));
                physicalExercisesCard.setStrokeColor(getColor(R.color.card_stroke_color));
                physicalExercisesCard.setCheckedIcon(ContextCompat.getDrawable(this, R.drawable.blank));
                continueButton.setVisibility(ExtendedFloatingActionButton.VISIBLE);
            }
        });
        continueButton.setOnClickListener(v -> {
            Intent exercise = new Intent(MainActivity.this, ExerciseListActivity.class);
            exercise.putExtra("exerciseType", exerciseType);
            startActivity(exercise);
        });
        animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_alpha_animation);
        welcomeText.setText("Hi " + name +"! What would you like to do today?");
        fitSense.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                audioHelper.startRecording(getExternalFilesDir(null) + "/audio.mp4");
                startCheckingAudioLevel();
                closeButton.setVisibility(ImageView.VISIBLE);
              /*  //Start animation
                animatingDialog = new Dialog(MainActivity.this);
                animatingDialog.setContentView(R.layout.animation_dialog);
                animatingDialog.setCancelable(false);
                animatingDialog.show();
                Animation animation = android.view.animation.AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_alpha_animation);*/

            }else {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 101);
            }
        });
        closeButton.setOnClickListener(v -> {
            audioHelper.stopRecording();
            stopCheckingAudioLevel();
            closeButton.setVisibility(ImageView.GONE);
            if (voiceButton.getVisibility() == ImageView.VISIBLE) {
                voiceButton.setVisibility(ImageView.GONE);
            }
        });
    }

    private void generateSpeech(String text) {
        networkHelper.generateSpeech(text, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "TTS generation failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Save the response body as an audio file
                    InputStream inputStream = response.body().byteStream();
                    File outputFile = new File(getExternalFilesDir(null), "output_speech.mp3");
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.flush();
                    } catch (IOException e) {
                        Log.e("MainActivity", "Failed to save TTS response", e);
                    }

                    // Play the audio file
                    runOnUiThread(() -> playAudio(outputFile.getAbsolutePath(),text));
                } else {
                    Log.e("MainActivity", "TTS generation failed with status code: " + response.code());
                    // Log the error body for more detailed information
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e("MainActivity", "TTS error body: " + errorBody);
                }
            }
        });
    }

    private void playAudio(String audioFilePath, String text) {
        closeButton.setVisibility(ImageView.GONE);
        voiceButton.startAnimation(animation);
        voiceButton.setVisibility(ImageView.VISIBLE);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // messageAdapter.startLoadingAnimation();
            //    mediaPlayer.setOnCompletionListener(mp -> messageAdapter.stopScaleAnimation());
            mediaPlayer.setOnCompletionListener(mp -> {
                audioHelper.startRecording(getExternalFilesDir(null) + "/audio.mp4");
                startCheckingAudioLevel();
                closeButton.setVisibility(ImageView.VISIBLE);
                voiceButton.setVisibility(ImageView.GONE);
            });
        } catch (IOException e) {
            Log.e("MainActivity", "Audio playback failed", e);
        }
       // addResponse(text.trim());
    }

    private void startCheckingAudioLevel() {
        checkAudioLevelRunnable = new Runnable() {
            @Override
            public void run() {
                // Get the current audio level
                int audioLevel = audioHelper.getAudioLevel();
                Log.d("AudioLevel", "Current audio level: " + audioLevel);

                if (audioLevel < AUDIO_LEVEL_THRESHOLD) {
                    if (silenceStartTime == -1) {
                        silenceStartTime = System.currentTimeMillis();
                    }

                    long silenceDuration = System.currentTimeMillis() - silenceStartTime;

                    // Stop recording if silence lasts more than the threshold
                    if (silenceDuration > SILENCE_DURATION_THRESHOLD) {
                        audioHelper.stopRecording();
                        transcribeAudio(audioHelper.getAudioFilePath());
                        silenceStartTime = -1;
                        stopCheckingAudioLevel();
                        return;
                    }
                } else {
                    // Reset silence timer if noise is detected
                    silenceStartTime = -1;
                }
                handler.postDelayed(this, 2000); // Run this code every 5 seconds
            }
        };
        handler.post(checkAudioLevelRunnable); // Start the loop
    }

    private void stopCheckingAudioLevel() {
        if (handler != null && checkAudioLevelRunnable != null) {
            handler.removeCallbacks(checkAudioLevelRunnable); // Stop the periodic task
        }
    }

    private void transcribeAudio(String filePath) {
        networkHelper.transcribeAudio(filePath, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Wait.. Can you say that again?", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String text = jsonObject.getString("text");
                        Log.i("Transcription", "Transcribed text: " + text);
                        callAPI(text);
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Wait.. Can you say that again?", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Wait.. Can you say that again?", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void textToSpeech(String text) {
        runOnUiThread(() -> textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null));
    }

    void callAPI(String msg) {
        Log.d("TAG", "callAPI: "+jsonArray);
        //messageList.add(new MessageModel("Thinking...", MessageModel.SEND_BY_BOT));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-4o");
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("role", "user");
            jsonObject2.put("content", msg);
            jsonArray.put(jsonObject2);
            jsonObject.put("messages", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer "+BuildConfig.API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject1 = new JSONObject(res);
                        String text = jsonObject1.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                        Log.d("Transcription", "Response text: " + text);
                        generateSpeech(text.trim());
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("role", "system");
                        jsonObject2.put("content", text.trim());
                        jsonArray.put(jsonObject2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                //    addResponse("Sorry can you repeat that again? "+response.body().string());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
              //  addResponse("Sorry can you repeat that again? "+e.getMessage());
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This Language is not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Initialization Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}