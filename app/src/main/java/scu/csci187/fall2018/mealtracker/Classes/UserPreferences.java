package scu.csci187.fall2018.mealtracker.Classes;

public class UserPreferences {

    public int calorieLow;  // if both calorieLow = calorieHigh == 0, user did not specify a calorie range
    public int calorieHigh;
    public int maxTimeInMinutes;    // if maxTimeInMinutes == 0, user did not specify a max time
    public int dietLabel;  // 0:none, 1:lowcarb, 2:lowfat, 3:highprotein, 4:highfiber, 5:lowsodium
    public boolean vegetarian;
    public boolean vegan;
    public boolean pescatarian;
    public boolean kosher;
    public boolean gluten;
    public boolean paleo;
    public boolean shellfish;
    public boolean dairy;
    public boolean treenut;
    public boolean peanut;
    public boolean egg;

    public UserPreferences(int calorieLow, int calorieHigh, int maxTimeInMinutes, int dietLabel,
                           boolean vegetarian, boolean vegan, boolean pescatarian,
                           boolean kosher, boolean gluten, boolean paleo, boolean shellfish,
                           boolean dairy, boolean treenut, boolean peanut, boolean egg) {
        this.calorieLow = calorieLow;
        this.calorieHigh = calorieHigh;
        this.maxTimeInMinutes = maxTimeInMinutes;
        this.dietLabel = dietLabel;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.pescatarian = pescatarian;
        this.kosher = kosher;
        this.gluten = gluten;
        this.paleo = paleo;
        this.shellfish = shellfish;
        this.dairy = dairy;
        this.treenut = treenut;
        this.peanut = peanut;
        this.egg = egg;
    }

    public int getCalorieLow() {
        return calorieLow;
    }

    public int getCalorieHigh() {
        return calorieHigh;
    }

    public int getMaxTimeInMinutes() {
        return maxTimeInMinutes;
    }

    public int getDietLabel() {
        return dietLabel;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public boolean isVegan() {
        return vegan;
    }

    public boolean isPescatarian() {
        return pescatarian;
    }

    public boolean isKosher() {
        return kosher;
    }

    public boolean isGluten() {
        return gluten;
    }

    public boolean isPaleo() {
        return paleo;
    }

    public boolean isShellfish() {
        return shellfish;
    }

    public boolean isDairy() {
        return dairy;
    }

    public boolean isTreenut() {
        return treenut;
    }

    public boolean isPeanut() {
        return peanut;
    }

    public boolean isEgg() {
        return egg;
    }

    public String healthLabelToString(){
        StringBuilder sb1 = new StringBuilder();

        if(isVegetarian()) sb1.append('1');
        else sb1.append('0');

        if(isVegan()) sb1.append('1');
        else sb1.append('0');

        if(isPescatarian()) sb1.append('1');
        else sb1.append('0');

        if(isKosher()) sb1.append('1');
        else sb1.append('0');

        if(isGluten()) sb1.append('1');
        else sb1.append('0');

        if(isPaleo()) sb1.append('1');
        else sb1.append('0');

        if(isShellfish()) sb1.append('1');
        else sb1.append('0');

        if(isDairy()) sb1.append('1');
        else sb1.append('0');

        if(isTreenut()) sb1.append('1');
        else sb1.append('0');

        if(isPeanut()) sb1.append('1');
        else sb1.append('0');

        if(isEgg()) sb1.append('1');
        else sb1.append('0');

        return sb1.toString();
    }

}
