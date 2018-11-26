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
        int month = Integer.parseInt(splitInput[monthIndex]);
        int day = Integer.parseInt(splitInput[dayIndex]);
        int year = Integer.parseInt(splitInput[yearIndex]);

        this.date = new GregorianCalendar(year, month, day).getTime();


    }

    public RecipeRecord (String bookmarkURL, String name, String dateString, int time) {
        this.bookmarkURL = bookmarkURL;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getDate() {
        return this.date;
    }

    public int getTime() {
        return time;
    }
}