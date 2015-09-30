package uha.ensisa.android.wishalert;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends ActionBarActivity {

    SQLiteDatabase db;

    ListView list;
    HomeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = openOrCreateDatabase("Events.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS events(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, phone VARCHAR, date VARCHAR, type VARCHAR, isAlarm VARCHAR, isMessage VARCHAR, message VARCHAR);");

        list = (ListView) findViewById(R.id.list);
        adapter = new HomeListAdapter(this, getEvents());
        list.setAdapter(adapter);

        registerForContextMenu(list);

        Context context = this.getApplicationContext();
    }

    //Load saved events in the list to be shown at home
    private List getEvents() {

        List<Event> events = new ArrayList<Event>();

        Cursor c=db.rawQuery("SELECT * FROM events", null);
        while(c.moveToNext())
        {
            Event event = new Event();
            event.setId(Integer.parseInt(c.getString(0)));
            event.setName(c.getString(1));
            event.setPhone(c.getString(2));
            event.setDate(c.getString(3));
            event.setType(c.getString(4));
            event.setIsAlarm(Boolean.valueOf(c.getString(5)));
            event.setIsMessage(Boolean.valueOf(c.getString(6)));
            event.setMessage(c.getString(7));

            events.add(event);
        }

        return events;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.add) { //Launch add new activity
            Intent nextScreen = new Intent(getApplicationContext(), AddEventActivity.class);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = menuInfo.position; //position in the adapter
        long itemId = menuInfo.id;
        int id = item.getItemId();

        if(id == R.id.delete) { //Delete entry from table
            Event e = (Event) list.getItemAtPosition((int)itemId);
            db.execSQL("DELETE FROM events WHERE id=" + e.getId());
            adapter.removeAt((int)itemId);
            adapter.notifyDataSetChanged();
        }
        else if (id == R.id.edit) { //Launch editor activity
            Event e = (Event) list.getItemAtPosition((int)itemId);

            Intent nextScreen = new Intent(getApplicationContext(), EditEventActivity.class);
            nextScreen.putExtra("id", Integer.toString(e.getId()));
            startActivity(nextScreen);
        }
        return super.onContextItemSelected(item);
    }
}
