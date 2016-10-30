package app.jongyeop2.fireinthehouse;

/**
 * Created by Han on 2016-10-24.
 */

public class SmsItem {
    private String name;
    private String phoneNumber;

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }

    public SmsItem(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public SmsItem(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
