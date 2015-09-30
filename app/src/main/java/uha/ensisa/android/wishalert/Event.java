package uha.ensisa.android.wishalert;

//DB Schema
public class Event {

    private int id;
    private String name;
    private String phone;
    private String type;
    private String date;
    private String message;

    public Boolean getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Boolean isAlarm) {
        this.isAlarm = isAlarm;
    }

    public Boolean getIsMessage() {
        return isMessage;
    }

    public void setIsMessage(Boolean isMessage) {
        this.isMessage = isMessage;
    }

    private Boolean isAlarm;
    private Boolean isMessage;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
