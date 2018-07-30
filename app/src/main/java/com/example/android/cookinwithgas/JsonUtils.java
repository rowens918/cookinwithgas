package com.example.android.cookinwithgas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_SERVINGS = "servings";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_INGREDIENTS = "ingredients";
    private static final String TAG_QUANTITY = "quantity";
    private static final String TAG_MEASURE = "measure";
    private static final String TAG_INGREDIENT = "ingredient";
    private static final String TAG_STEPS = "steps";
    private static final String TAG_STEP_ID = "id";
    private static final String TAG_TITLE = "shortDescription";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_VIDEOURL = "videoURL";
    private static final String TAG_THUMBNAILURL = "thumbnailURL";


    public static List<RecipeInfo> parseRecipeData(String rawJson) {
        List<RecipeInfo> list = new ArrayList<>();

        try {
            RecipeInfo tempRecipe = new RecipeInfo();
            JSONArray jsonArray = new JSONArray(rawJson);

            for (int i = 0; i < jsonArray.length(); i++) {
                tempRecipe = new RecipeInfo();
                JSONObject object = jsonArray.getJSONObject(i);
                List<IngredientInfo> ingredientsList = new ArrayList<>();
                List<StepInfo> stepsList = new ArrayList<>();

                tempRecipe.recipeId = object.optString(TAG_ID);
                tempRecipe.recipeName = object.optString(TAG_NAME);
                tempRecipe.recipeServings = object.optString(TAG_SERVINGS);
                tempRecipe.recipeImage = object.optString(TAG_IMAGE);

                //Ingredient Info
                JSONArray ingredients = object.getJSONArray(TAG_INGREDIENTS);
                for (int x = 0; x < ingredients.length(); x++) {
                    IngredientInfo tempIngredient = new IngredientInfo();
                    tempIngredient.ingredientQuantity = (float) ingredients.getJSONObject(x).optDouble(TAG_QUANTITY);
                    tempIngredient.ingredientMeasure = ingredients.getJSONObject(x).optString(TAG_MEASURE);
                    tempIngredient.ingredientName = ingredients.getJSONObject(x).optString(TAG_INGREDIENT);
                    ingredientsList.add(x, tempIngredient);
                }
                tempRecipe.recipeIngredients = ingredientsList;

                //Step Info
                JSONArray steps = object.getJSONArray(TAG_STEPS);

                for (int y = 0; y < steps.length(); y++) {
                    StepInfo tempStep = new StepInfo();
                    tempStep.stepId = steps.getJSONObject(y).optString(TAG_STEP_ID);
                    tempStep.stepTitle = steps.getJSONObject(y).optString(TAG_TITLE);
                    tempStep.stepDescription = steps.getJSONObject(y).optString(TAG_DESCRIPTION);
                    tempStep.stepVideo = steps.getJSONObject(y).optString(TAG_VIDEOURL);
                    tempStep.stepImage = steps.getJSONObject(y).optString(TAG_THUMBNAILURL);
                    stepsList.add(y, tempStep);
                }
                tempRecipe.recipeSteps = stepsList;

                list.add(i, tempRecipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
