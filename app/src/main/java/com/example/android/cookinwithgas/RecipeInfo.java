package com.example.android.cookinwithgas;

import java.util.List;

public class RecipeInfo {
    String recipeId;
    String recipeName;
    List<IngredientInfo> recipeIngredients;
    List<StepInfo> recipeSteps;
    String recipeServings;
    String recipeImage;

    public RecipeInfo(){

    }

    public RecipeInfo(String recipeId, String name, List<IngredientInfo> ingredients, List<StepInfo> steps, String servings, String image) {
        this.recipeId = recipeId;
        this.recipeName = name;
        this.recipeIngredients = ingredients;
        this.recipeSteps = steps;
        this.recipeServings = servings;
        this.recipeImage = image;
    }
}
