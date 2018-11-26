package scu.csci187.fall2018.mealtracker.Classes;
import java.util.Comparator;

public class RecipeRecordComparator implements Comparator<RecipeRecord> {

    @Override
    public int compare(RecipeRecord comp1, RecipeRecord comp2) {

        int comparison = comp1.getDate().compareTo(comp2.getDate());
        if (comparison == 0){
            if (comp1.getTime() >= comp2.getTime()) {
                return 1;
            } else {
                return -1;
            }
        } else if (comparison > 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
