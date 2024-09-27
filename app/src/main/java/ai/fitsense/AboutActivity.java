package ai.fitsense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {
    TextView exerciseSteps;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        exerciseSteps = findViewById(R.id.exerciseSteps);
        startButton = findViewById(R.id.startButton);
        String steps = "Step 1: Stand up straight with a dumbbell in each hand at arm's length. Keep your elbows close to your torso and rotate the palms of your hands until they are facing forward. This will be your starting position.\n" +
                "Step 2: Now, keeping the upper arms stationary, exhale and curl the weights while contracting your biceps. Continue to raise the weights until your biceps are fully contracted and the dumbbells are at shoulder level. Hold the contracted position for a brief pause as you squeeze your biceps.\n" +
                "Step 3: Then, inhale and slowly begin to lower the dumbbells back to the starting position.\n" +
                "Repeat for the recommended amount of repetitions.\n" +
                "Variations: There are many possible variations for the hammer curls. For example, they can be performed sitting down on a bench with or without back support and you can also perform the exercise one arm at a time.\n" +
                "Also, you can perform the exercise by alternating arms; first lift the right arm for one repetition, then the left, then the right, etc.\n" +
                "Caution: Avoid swinging the torso and using momentum to lift the weights. Also, do not hold your breath. ";
        exerciseSteps.setText(steps);
        startButton.setOnClickListener(v -> {
           startActivity(new Intent(this, ExerciseActivity.class));
        });
    }
}