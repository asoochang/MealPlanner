package scu.csci187.fall2018.mealtracker.Classes;
import java.util.Comparator;

public class RecipeRecordComparator implements Comparator<RecipeRecord> {

    @Override
    public int compare(RecipeRecord comp1, RecipeRecord comp2) {

        return comp1.getDate().compareTo(comp2.getDate());
    }
}
