package scu.csci187.fall2018.mealtracker.Classes;
import java.util.Date;
import java.util.GregorianCalendar;

public class RecipeRecord {

    private String bookmarkURL;
    private String name;
    private String dateString;
    private int time;
    private Date date;

    private void getDateFromString(String input) {
        String[] splitInput = input.split("/");

        int monthIndex = 0, dayIndex = 1, yearIndex = 2;
        // Subtract 1 because month is indexed starting at 0 for Jan
        // And values inputted are in standard human month numbering
        int month = Integer.parseInt(splitInput[monthIndex]) - 1;
        int day = Integer.parseInt(splitInput[dayIndex]);
        int year = Integer.parseInt(splitInput[yearIndex]);

        this.date = new GregorianCalendar(year, month, day).getTime();
    }

    public RecipeRecord (String bookmarkURL, String dateString, int time) {
        this.bookmarkURL = bookmarkURL;
        this.dateString = dateString;
        this.time = time;
        this.getDateFromString(this.dateString);
    }

    public String getBookmarkURL() {
        return bookmarkURL;
    }

    public void setBookmarkURL(String bookmarkURL) {
        this.bookmarkURL = bookmarkURL;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {

        this.dateString = dateString;
        this.getDateFromString(this.dateString);
    }

    public boolean isInHistory() {
        Date currentDate = new Date();
        boolean comparisonOfYears = this.getDate().getYear() >= currentDate.getYear();
        boolean comparisonOfMonths = this.getDate().getMonth() >= currentDate.getMonth();
        boolean comparisonOfDays = this.getDate().getDay() >= currentDate.getDay();

        if (comparisonOfYears){
            if (comparisonOfMonths){
                if (comparisonOfDays){
                    return false;
                }
            }

        }
        return true;
    }

    public Date getDate() {
        return this.date;
    }

    public int getTime() {
        return time;
    }
}