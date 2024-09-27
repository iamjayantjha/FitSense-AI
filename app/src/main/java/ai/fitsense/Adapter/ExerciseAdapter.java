package ai.fitsense.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ai.fitsense.AboutActivity;
import ai.fitsense.R;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    String[] exercises;

    public ExerciseAdapter(String[] exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.exerciseName.setText(exercises[position]);
        holder.exerciseCard.setOnClickListener(v -> {
           if (holder.exerciseName.getText().toString().equalsIgnoreCase("Hammer Curl")) {
               holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), AboutActivity.class));
           }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.length;
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        MaterialCardView exerciseCard;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseCard = itemView.findViewById(R.id.exerciseCard);
        }
    }
}
