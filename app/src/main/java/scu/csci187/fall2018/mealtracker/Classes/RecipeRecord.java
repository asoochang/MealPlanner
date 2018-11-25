package scu.csci187.fall2018.mealtracker.Classes;
import java.util.Date;
import java.util.GregorianCalendar;

public class RecipeRecord {

    private String bookmarkURL;
    private String name;
    private String dateString;
    private String picURL;
    private Date date;

    private void getDateFromString(String input) {
        String[] splitInput = input.split("/");

        int monthIndex = 0, dayIndex = 1, yearIndex = 2;
        int month = Integer.parseInt(splitInput[monthIndex]);
        int day = Integer.parseInt(splitInput[dayIndex]);
        int year = Integer.parseInt(splitInput[yearIndex]);

        this.date = new GregorianCalendar(year, month, day).getTime();
    }

    public RecipeRecord (String bookmarkURL, String name, String dateString, String picURL) {
        this.bookmarkURL = bookmarkURL;
        this.name = name;
        this.dateString = dateString;
        this.picURL = picURL;
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

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {

        this.dateString = dateString;
        this.getDateFromString(this.dateString);
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public Date getDate() {
        return this.date;
    }

}