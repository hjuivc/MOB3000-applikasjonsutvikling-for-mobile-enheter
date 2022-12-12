package com.example.recipeapp;

import android.content.res.Resources;
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

        // Translates units to the given device language (English or Norwegian)
        String unit = model.getUnit();
        String new_unit = null;
        Resources res = holder.itemView.getContext().getResources();
        String language = res.getConfiguration().locale.getLanguage();

        if (language.equals("en"))
            if (unit.equals("stk")) {
                new_unit = unit.replace("stk", "pcs");
            } else if (unit.equals("ts")) {
                new_unit = unit.replace("ts", "tsp");
            } else if (unit.equals("ss")) {
                new_unit = unit.replace("ss", "tbsp");
            } else if (unit.equals("dl")) {
                new_unit = unit.replace("dl", "dl");
            } else if (unit.equals("l")) {
                new_unit = unit.replace("l", "l");
            } else if (unit.equals("kg")) {
                new_unit = unit.replace("kg", "kg");
            } else if (unit.equals("g")) {
                new_unit = unit.replace("g", "g");
            } else {
                new_unit = unit;
            }

        if (language.equals("nb")) {
            if (unit.equals("pcs")) {
                new_unit = unit.replace("pcs", "stk");
            } else if (unit.equals("tsp")) {
                new_unit = unit.replace("tsp", "ts");
            } else if (unit.equals("tbsp")) {
                new_unit = unit.replace("tbsp", "ss");
            } else if (unit.equals("dl")) {
                new_unit = unit.replace("dl", "dl");
            } else if (unit.equals("l")) {
                new_unit = unit.replace("l", "l");
            } else if (unit.equals("kg")) {
                new_unit = unit.replace("kg", "kg");
            } else if (unit.equals("g")) {
                new_unit = unit.replace("g", "g");
            }
            else {
                new_unit = unit;
            }
        }

        if (new_unit != null) {
            holder.ingredientUnit.setText(new_unit);
        } else {
            holder.ingredientUnit.setText(model.getUnit());
        }

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
