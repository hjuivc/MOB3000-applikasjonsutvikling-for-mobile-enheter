package com.example.recipeapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RecipeRecAdapter extends FirebaseRecyclerAdapter<Recipe, RecipeRecAdapter.recipeViewholder> {

    public RecipeRecAdapter(@NonNull FirebaseRecyclerOptions<Recipe> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull recipeViewholder holder, int position, @NonNull Recipe model) {
        holder.recipeNameText.setText(model.getName());
        holder.recipeDescriptionText.setText(model.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecipeActivity.class);
                intent.putExtra("recipeId", getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public recipeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recipies, parent, false);
        return new RecipeRecAdapter.recipeViewholder(view);
    }

    class recipeViewholder extends RecyclerView.ViewHolder {
        TextView recipeNameText, recipeDescriptionText;

        public recipeViewholder(@NonNull View itemView) {
            super(itemView);

            recipeNameText = itemView.findViewById(R.id.recipeNameText);
            recipeDescriptionText = itemView.findViewById(R.id.recipeDescriptionText);
        }
    }
}
