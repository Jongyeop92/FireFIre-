package app.jongyeop2.fireinthehouse;

/**
 * Created by Han on 2016-10-26.
 */

public class FireHistoryItem {
    private String title;
    private String message;
    private String date;

    public FireHistoryItem(String title, String message, String date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
}
