package uha.ensisa.android.wishalert;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class EventsDb extends Activity {
    SQLiteDatabase db;

    public void init() {

    }

    public void addEvent(String name, String phone,  String date, String type) {
        db.execSQL("INSERT INTO events VALUES(default, '"+ name + "', '"+ phone + "', '" + date +"', '"+ type +"');");
    }

    public void updateEvent(int id, String name, String phone, String date, String type) {
        db.execSQL("UPDATE events SET name='"+ name + "', phone='" + phone +"', date='"+ date + "', type='" + type +"' WHERE id=" + id);
    }

    public Event getEvent(int id) {

        Event event = new Event();

        Cursor c=db.rawQuery("SELECT * FROM events WHERE id=" + id, null);
        if(c.moveToFirst())
        {
            event.setId(Integer.parseInt(c.getString(1)));
            event.setName(c.getString(2));
            event.setPhone(c.getString(3));
            event.setDate(c.getString(4));
            event.setType(c.getString(5));
        }
        return event;
    }

    public ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<Event>();

        Cursor c=db.rawQuery("SELECT * FROM events", null);
        while(c.moveToNext())
        {
            Event event = new Event();
            event.setId(Integer.parseInt(c.getString(1)));
            event.setName(c.getString(2));
            event.setPhone(c.getString(3));
            event.setDate(c.getString(4));
            event.setType(c.getString(5));

            events.add(event);
        }
        return events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("Events.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS events(id INTEGER PRIMARY KEY, name VARCHAR, phone VARCHAR, date VARCHAR, type VARCHAR);");
    }
}
