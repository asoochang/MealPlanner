package scu.csci187.fall2018.mealtracker.Classes;

public class PreferencesTranslator {

    public PreferencesTranslator(){
        // Nothing to see here
    }

    public void setDietInQueryParam(UserPreferences userPref, QueryParam qp) {
        if (userPref == null) {
            qp.setDiet("");
        } else {
            switch (userPref.getDietLabel()) {
                case -1:
                    qp.setDiet("");
                    return;
                case 0:
                    qp.setDiet("low-carb");
                    return;
                case 1:
                    qp.setDiet("low-fat");
                    return;
                case 2:
                    qp.setDiet("high-protein");
                    return;
                case 3:
                    qp.setDiet("high-fiber");
                    return;
                case 4:
                    qp.setDiet("low-sodium");
                    return;
                default:
                    qp.setDiet("");
                    return;
            }
        }
    }

    public void setHealthLabelsInQueryParam(UserPreferences userPref, QueryParam qp) {

        if (userPref == null) {
            // userPref not set so everything gets default value
            // to be handled by QueryParam upon assembly.
            qp.setCalorieMin(0);
            qp.setCalorieMax(0);
            qp.setTimeMin(0);
            qp.setTimeMax(0);
        } else {
            qp.setCalorieMin(userPref.getCalorieLow());
            qp.setCalorieMax(userPref.getCalorieHigh());
            qp.setTimeMin(0);
            qp.setTimeMax(userPref.getMaxTimeInMinutes());

            if (userPref.isVegan()) {
                qp.addHealthLabels("vegan");
            }
            if (userPref.isVegetarian()) {
                qp.addHealthLabels("vegetarian");
            }
            if (userPref.isDairy()) {
                qp.addHealthLabels("dairy-free");
            }
            if (userPref.isEgg()) {
                qp.addHealthLabels("egg-free");
            }
            if (userPref.isGluten()) {
                qp.addHealthLabels("gluten-free");
            }
            if (userPref.isKosher()) {
                qp.addHealthLabels("kosher");
            }
            if (userPref.isPaleo()) {
                qp.addHealthLabels("paleo");
            }
            if (userPref.isPescatarian()) {
                qp.addHealthLabels("pescatarian");
            }
            if (userPref.isShellfish()) {
                qp.addHealthLabels("shellfish-free");
            }
            if (userPref.isPeanut()) {
                qp.addHealthLabels("peanut-free");
            }
            if (userPref.isTreenut()) {
                qp.addHealthLabels("tree-nut-free");
            }
        }
    }
}
