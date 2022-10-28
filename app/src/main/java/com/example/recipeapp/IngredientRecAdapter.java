package com.example.recipeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class IngredientRecAdapter  extends FirebaseRecyclerAdapter <Ingredients, IngredientRecAdapter.ingredientViewholder> {

        public IngredientRecAdapter(@NonNull FirebaseRecyclerOptions<Ingredients> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull ingredientViewholder holder, int position, @NonNull Ingredients model) {
            holder.ingredientName.setText(model.getIngredient());
            holder.ingredientQuantity.setText(model.getAmount());
            holder.ingredientUnit.setText(model.getUnit());
        }

        @NonNull
        @Override
        public ingredientViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ingredients, parent, false);
            return new ingredientViewholder(view);
        }

        class ingredientViewholder extends RecyclerView.ViewHolder {
            TextView ingredientName, ingredientQuantity, ingredientUnit;

            public ingredientViewholder(@NonNull View itemView) {
                super(itemView);
                ingredientName = itemView.findViewById(R.id.ingredient);
                ingredientQuantity = itemView.findViewById(R.id.amount);
                ingredientUnit = itemView.findViewById(R.id.unit);
            }
        }
}
