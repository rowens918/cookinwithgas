package com.example.android.cookinwithgas;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;




/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private RecipeInfo mItem;
    private View rootView;
    private int currStep = 0;
    private Button nextButton;
    private Button backButton;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    boolean playWhenReady = false;
    int currentWindow = 0;
    long playbackPosition = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            int position = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            mItem = RecipeListActivity.Recipes.get(position-1);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.recipeName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        nextButton = rootView.findViewById(R.id.next_step);
        backButton = rootView.findViewById(R.id.back_step);
        playerView = rootView.findViewById(R.id.exo_video);

        // Button Listeners derived from Slack conversation with Tane Tachyon
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setVisibility(View.VISIBLE);
                if(currStep + 1 <= mItem.recipeSteps.size() - 1) {
                    onClickNextStep(v);
                }

                if (currStep >= mItem.recipeSteps.size() -1) {
                    nextButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setVisibility(View.VISIBLE);
                if(currStep >= 1) {
                    onClickBackStep(v);
                } else {
                    backButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_recipe_title)).setText(mItem.recipeName);
            ((TextView) rootView.findViewById(R.id.tv_recipe_servings)).setText(mItem.recipeServings);
            String ingredients = "";

            List<IngredientInfo> ingList = mItem.recipeIngredients;

            for (int i = 0; i < ingList.size(); i++) {
                IngredientInfo tempIngredient = ingList.get(i);

                String name = tempIngredient.ingredientName;
                float quantity = tempIngredient.ingredientQuantity;
                String measure = tempIngredient.ingredientMeasure;
                ingredients = ingredients + quantity + " " + measure + " of " + name + "\n";
            }
            ((TextView) rootView.findViewById(R.id.tv_recipe_ingredients)).setText(ingredients);

            RecipeWidget.setRecipeWidgetText(getContext(), ingredients);

            changeStep(currStep);
        }
        return rootView;
    }


    // Much of the ExoPlayer code below here was yoinked and massaged gently into place based off of
    // this tutorial: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        StepInfo tempStep = mItem.recipeSteps.get(currStep);
        Uri uri = Uri.parse(tempStep.stepVideo);
        MediaSource video = buildMediaSource(uri);
        player.prepare(video, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
    }

    public void onClickNextStep(View view) {
            currStep++;
            changeStep(currStep);
    }

    public void onClickBackStep(View view) {
        currStep--;
        changeStep(currStep);
    }

    private View changeStep(int id) {
        if (id <= mItem.recipeSteps.size()) {
            ImageView ivStepImage = rootView.findViewById(R.id.iv_step_image);
            StepInfo tempStep = mItem.recipeSteps.get(id);
            currStep = id;

            if (currStep == 0) {
                backButton.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.VISIBLE);
            }


            ((TextView) rootView.findViewById(R.id.tv_recipe_step_id)).setText(tempStep.stepId);
            ((TextView) rootView.findViewById(R.id.tv_recipe_step_title)).setText(tempStep.stepTitle);
            ((TextView) rootView.findViewById(R.id.tv_recipe_step_description)).setText(tempStep.stepDescription);
            if (!tempStep.stepImage.equals("")) {
                try {
                    Uri builtUri = Uri.parse(tempStep.stepImage).buildUpon().build();
                    URL url = new URL(builtUri.toString());
                    Picasso.with(getContext()).load(url.toString()).into(ivStepImage);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                ivStepImage.setVisibility(View.INVISIBLE);
            }

            if (!tempStep.stepVideo.equals("")) {
                playerView.setVisibility(View.VISIBLE);
                initializePlayer();
            } else {
                playerView.setVisibility(View.INVISIBLE);
                releasePlayer();
            }
        }
        return rootView;
    }
}
