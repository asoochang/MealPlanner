package scu.csci187.fall2018.mealtracker.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
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
    private int myYear, myMonth, myDay, bldChoice;

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

            if(showMadeButton)
                buttonMadeThis.setVisibility(View.VISIBLE);
        }
        setupRatingBarAndFavorite();
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
                    Toast t = Toast.makeText(getContext(), "Meal removed from favorites list", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
                }
                else {
                    mealIsFavorited = true;
                    ivFavorite.setImageResource(R.drawable.ic_favorite);
                    updateMealFavoriteInDB(mealIsFavorited);
                    Toast t = Toast.makeText(getContext(), "Meal added to favorites list", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
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

                DatePickerDialog picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                setDateVals(year, month+1, dayOfMonth);

                                AlertDialog.Builder chooseBldDialog = new AlertDialog.Builder(getContext());
                                chooseBldDialog.setTitle("Choose a meal time: ");

                                ArrayList<String> bldStrings = new ArrayList<>();
                                bldStrings.add("Breakfast");
                                bldStrings.add("Lunch");
                                bldStrings.add("Dinner");
                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice, bldStrings);


                                chooseBldDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int index) {
                                        dialog.dismiss();
                                    }
                                });
                                chooseBldDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int index) {
                                        setBLDchoice(index);
                                        String mealOfDay = "";
                                        switch(bldChoice) {
                                            case 0: mealOfDay = "Breakfast"; break;
                                            case 1: mealOfDay = "Lunch"; break;
                                            case 2: mealOfDay = "Dinner"; break;
                                        }
                                        Toast t = Toast.makeText(getContext(), "Scheduled " + mealName + " for " + mealOfDay + " on " +
                                                myMonth + "/" + myDay + "/" + myYear, Toast.LENGTH_LONG);
                                        t.setGravity(Gravity.CENTER, 0,0);
                                        t.show();
                                    }
                                });
                                chooseBldDialog.show();
                                scheduleMealInDB(year, month+1, dayOfMonth, bldChoice);

                            }
                        }, year, month, day);
                picker.show();
                sendToShoppingList(bookmarkURL);
            }
        });
    }

    private void setupRatingBarAndFavorite() {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());

        mealIsFavorited = myDB.isFavorite(bookmarkURL);
        if(mealIsFavorited)
            ivFavorite.setImageResource(R.drawable.ic_favorite);
        else
            ivFavorite.setImageResource(R.drawable.ic_favorite_no);
        mealRating = myDB.getRating(bookmarkURL);
        mealRatingBar.setRating(mealRating);
    }

    private void updateUserMealRatingInDB(int newRating) {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.updateRating(bookmarkURL, newRating);
    }

    private void updateMealFavoriteInDB(boolean isFavorited) {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        if(isFavorited)
            myDB.addToFavorites(bookmarkURL);
        else
            myDB.removeFromFavorites(bookmarkURL);
    }

    private void scheduleMealInDB(int year, int month, int day, int bldChoice) {
        String date = month + "/" + day + "/" + year;
        int mealNO = bldChoice;
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.addMeal(date, bookmarkURL, mealNO);
    }

    private void updateMadeMealInDB() {
        SQLiteUserManager myDB = new SQLiteUserManager(getContext());
        myDB.flagMeal(bookmarkURL);
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

    private void setDateVals(int year, int month, int day) {
        myYear = year;
        myMonth = month;
        myDay = day;
    }

    private void setBLDchoice(int index) {
        bldChoice = index;
    }

    public interface ScheduleMealListener {
        void showHomeScreenAfterScheduleMeal();

        void sendToShoppingListFromMealFragment(String bookmarkURL);
    }

    public interface MadeMealListener {
        void afterMadeMealClick();
    }
    public void sendToShoppingList(String bookmarkURL) {
        scheduleMealListener.sendToShoppingListFromMealFragment(bookmarkURL);
    }
}
