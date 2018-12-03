package scu.csci187.fall2018.mealtracker.Classes;
import java.util.Calendar;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

public class RecipeRecord {

    private String bookmarkURL;
    private String name;
    private String dateString;
    private int time;
    private Calendar date;

    private void getDateFromString(String input) {
        String[] splitInput = input.split("/");

        int monthIndex = 0, dayIndex = 1, yearIndex = 2;
        // Subtract 1 because month is indexed starting at 0 for Jan
        // And values inputted are in standard human month numbering
        int month = Integer.parseInt(splitInput[monthIndex]);
        int day = Integer.parseInt(splitInput[dayIndex]);
        int year = Integer.parseInt(splitInput[yearIndex]);

        this.date = new GregorianCalendar(year, month, day);
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

    public boolean isInFuture() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(currentDate.MINUTE,0);
        currentDate.set(currentDate.SECOND,0);
        currentDate.set(currentDate.MILLISECOND,0);

        System.out.println("MyDate >> "+  this.getDate().toString());
        System.out.println("CurrDate >>" +  currentDate.toString());
        System.out.flush();
        boolean myYearIsInFuture = this.getDate().get(Calendar.YEAR) >= currentDate.get(Calendar.YEAR);
        boolean myMonthIsInFuture = this.getDate().get(Calendar.MONTH) >= currentDate.get(Calendar.MONTH);
        boolean myDayIsInFuture = this.getDate().get(Calendar.DAY_OF_MONTH) >= currentDate.get(Calendar.DAY_OF_MONTH);

        if (myYearIsInFuture)
            if (myMonthIsInFuture)
                if (myDayIsInFuture)
                    return true;

        return false;
    }

    public Calendar getDate() {
        return this.date;
    }

    public int getTime() {
        return time;
    }
}