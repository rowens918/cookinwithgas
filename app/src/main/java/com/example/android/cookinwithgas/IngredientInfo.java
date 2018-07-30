package com.example.android.cookinwithgas;

public class IngredientInfo {
    String ingredientName;
    String ingredientMeasure;
    float ingredientQuantity;

    public IngredientInfo(){

    }

    public IngredientInfo(String name, String measure, float quantity) {
        this.ingredientName = name;
        this.ingredientMeasure = measure;
        this.ingredientQuantity = quantity;
    }
}
