package scu.csci187.fall2018.mealtracker.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import scu.csci187.fall2018.mealtracker.Activities.ViewRecipeActivity;
import scu.csci187.fall2018.mealtracker.Classes.APIHandler;
import scu.csci187.fall2018.mealtracker.Classes.ImageLoaderFromUrl;
import scu.csci187.fall2018.mealtracker.Classes.Ingredient;
import scu.csci187.fall2018.mealtracker.Classes.Ingredients;
import scu.csci187.fall2018.mealtracker.Classes.Recipe;
import scu.csci187.fall2018.mealtracker.Classes.RecipeRecord;
import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;


public class MealDetailFragment extends Fragment {
    private TextView tvMealName;
    private ImageView ivMealPic, ivFavorite;
    private RatingBar mealRatingBar;
    private ListView lvIngredients;
    private Button buttonToRecipe, buttonMadeThis, buttonSchedule;
    private Ingredients ingredients;
    private Recipe r;

    private String mealName, picURL, recipeURL, bookmarkURL;
    private boolean showMadeButton = false;
    private ArrayList<String> ingredientsList, bookmarks;
    private int mealRating;
    private int index = -1;
    private boolean mealIsFavorited;

    private MadeMealListener madeMealListener;
    private ScheduleMealListener scheduleMealListener;

    public MealDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookmarkURL = getArguments().getString("bookmarkURL");
            bookmarks = new ArrayList<>();
            bookmarks.add(bookmarkURL);
            r = new APIHandler().getRecipesFromBookmarks(bookmarks).get(0);
            picURL = r.imageUrl();
            mealName = r.name();
            recipeURL = r.linkToInstructions();
            index = getArguments().containsKey("index") ? getArguments().getInt("index") : -1;
            showMadeButton = getArguments().containsKey("madeThis") && getArguments().getBoolean("madeThis");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mealdetail_layout, container, false);

        if(mealName.isEmpty()) {
            getFragmentManager().popBackStackImmediate();   // go back to previous fragment
        }
        else {
            ingredientsList = new ArrayList<>();

            bindViews(view);
            attachUIListeners();
            populateMealData();
            setupRatingBarAndFavorite();
            if(showMadeButton)
                buttonMadeThis.setVisibility(View.VISIBLE);
        }

        return view;
    }

    // Ensures that Activity has implemented MadeMealListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MealDetailFragment.MadeMealListener ||
                context instanceof MealDetailFragment.ScheduleMealListener) {
            madeMealListener = (MealDetailFragment.MadeMealListener) context;
            scheduleMealListener = (MealDetailFragment.ScheduleMealListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement MadeMealListener and/or ScheduleMealListener");
        }
    }

    private void bindViews(View view) {
        tvMealName = view.findViewById(R.id.mealDetailName);
        ivMealPic = view.findViewById(R.id.mealDetailPic);
        ivFavorite = view.findViewById(R.id.buttonMealDetailFavorite);
        mealRatingBar = view.findViewById(R.id.mealRatingBar);
        lvIngredients = view.findViewById(R.id.ingredientsList);
        buttonToRecipe = view.findViewById(R.id.buttonGoToRecipe);
        buttonMadeThis = view.findViewById(R.id.buttonMadeThis);
        buttonSchedule = view.findViewById(R.id.buttonSchedule);
    }

    private void attachUIListeners() {
        attachRecipeButtonListener();
        attachRatingBarListener();
        attachFavoritesListener();
        attachMadeButtonListener();
        attachScheduleButtonListener();
    }
    private void attachRecipeButtonListener() {
        buttonToRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRecipe = new Intent(getContext(), ViewRecipeActivity.class);
                goToRecipe.putExtra("recipeURL", recipeURL);
                startActivity(goToRecipe);
            }
        });
    }

    private void attachRatingBarListener() {
        mealRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateUserMealRatingInDB((int)rating);
            }
        });
    }

    private void attachFavoritesListener() {
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mealIsFavorited) {
                    mealIsFavorited = false;
                    ivFavorite.setImageResource(R.drawable.ic_favorite_no);
                    updateMealFavoriteInDB(mealIsFavorited);
                }
                else {
                    mealIsFavorited = true;
                    ivFavorite.setImageResource(R.drawable.ic_favorite);
                    updateMealFavoriteInDB(mealIsFavorited);
                }
            }
        });
    }

    private void attachMadeButtonListener() {
        buttonMadeThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == -1)
                    Toast.makeText(getContext(), "ERROR - INDEX: -1, bundle index null", Toast.LENGTH_SHORT).show();
                else {
                    //TO TAKE OUT: (leave in case need to revert) madeMealListener.madeMealUpdateHistory(index);
                    updateMadeMealInDB();
                }
            }
        });
    }

    private void attachScheduleButtonListener() {
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                final int myYear, myMonth, myDay;
                DatePickerDialog picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                scheduleMealInDB(year, month+1, dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    private void setupRatingBarAndFavorite() {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());

        mealIsFavorited = myDB.isFavorite(recipeURL);
        if(mealIsFavorited)
            ivFavorite.setImageResource(R.drawable.ic_favorite);
        else
            ivFavorite.setImageResource(R.drawable.ic_favorite_no);
        mealRating = myDB.getRating(recipeURL);
        mealRatingBar.setRating(mealRating);
    }

    private void updateUserMealRatingInDB(int newRating) {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.updateRating(recipeURL, newRating);
    }

    private void updateMealFavoriteInDB(boolean isFavorited) {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        if(isFavorited)
            myDB.addToFavorites(recipeURL);
        else
            myDB.removeFromFavorites(recipeURL);
    }

    private void scheduleMealInDB(int year, int month, int day) {
        Toast.makeText(getContext(), "Scheduled " + mealName + " for " + month + "/" + day + "/" + year, Toast.LENGTH_LONG).show();
        String date = "filler";
        int mealNO = 5;
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.addMeal(date, recipeURL, mealNO);
    }

    private void updateMadeMealInDB() {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.flagMeal(recipeURL);
    }

    public void populateMealData() {
        tvMealName.setText(mealName);
        new ImageLoaderFromUrl(ivMealPic).execute(picURL);

        Ingredient ingredient;
        for (int i = 0; i < r.ingredients().length(); ++i) {
            ingredient = r.ingredients().getIngredientAtIndex(i);
            ingredientsList.add(ingredient.food());
        }


        ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, ingredientsList);
        lvIngredients.setAdapter(ingredientsAdapter);


        /*
            TODO: grab meal data from API, DB (rating) for display in MealDetail
            load ingredients strings into ingredientsList

            ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ingredientsList);
            lvIngredients.setAdapter(ingredientsAdapter);

            GRAB RATING FROM DB
            mealRating =
            mealRatingBar.setRating(mealRating);
         */


    }

    public interface ScheduleMealListener {
        void showHomeScreenAfterScheduleMeal();
    }

    public interface MadeMealListener {
        void afterMadeMealClick();
    }
}
