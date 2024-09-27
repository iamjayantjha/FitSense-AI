package ai.fitsense;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import ai.fitsense.Adapter.ExerciseAdapter;

public class ExerciseListActivity extends AppCompatActivity {
    TextView exerciseTypeTitle;
    RecyclerView exerciseList;
    String exerciseType = "";
    String[] gymExercises = {"Hammer Curl", "Bench Press", "Squats", "Deadlifts", "Pull-ups", "Dips", "Leg Press", "Shoulder Press", "Bicep Curls", "Tricep Extensions", "Calf Raises"};
    String[] physicalExercises = {"Push-ups", "Sit-ups", "Squats", "Lunges", "Planks", "Jumping Jacks", "Burpees", "Mountain Climbers", "Leg Raises", "Russian Twists", "Supermans"};
    String[] yogaExercises = {"Downward Dog", "Warrior Pose", "Tree Pose", "Child's Pose", "Cobra Pose", "Plank", "Triangle Pose", "Bridge Pose", "Pigeon Pose", "Seated Forward Bend", "Half Moon Pose"};
    String[] meditation = {"Mindfulness Meditation", "Body Scan Meditation", "Loving Kindness Meditation", "Breath Awareness Meditation", "Progressive Muscle Relaxation", "Visualization Meditation", "Mantra Meditation", "Yoga Nidra", "Chakra Meditation", "Walking Meditation", "Guided Meditation"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        exerciseTypeTitle = findViewById(R.id.exercise_type_title);
        exerciseList = findViewById(R.id.exercise_list);
        exerciseType = getIntent().getStringExtra("exerciseType");
        exerciseTypeTitle.setText(exerciseType);
        switch (exerciseType) {
            case "Workout":
                exerciseList.setAdapter(new ExerciseAdapter(gymExercises));
                break;
            case "Physical Exercise":
                exerciseList.setAdapter(new ExerciseAdapter(physicalExercises));
                break;
            case "Yoga":
                exerciseList.setAdapter(new ExerciseAdapter(yogaExercises));
                break;
            case "Meditation":
                exerciseList.setAdapter(new ExerciseAdapter(meditation));
                break;
        }
    }
}