package uha.ensisa.android.wishalert;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddEventActivity extends ActionBarActivity implements View.OnClickListener {

    private DatePickerDialog eventDatePickerDialog;

    private DatePickerDialog alarmDatePickerDialog;
    private TimePickerDialog alarmTimePickerDialog;

    //GUI elements
    private EditText name;
    private EditText phone;
    private Spinner type;
    private EditText eventDate;
    private EditText alarmDate;
    private EditText alarmTime;
    private CheckBox isAlarm;
    private CheckBox isMessage;
    private EditText message;

    //Form validation flag
    private Boolean isValidationComplete = true;

    //Date formatter
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    //Database object
    private SQLiteDatabase db;

    Calendar calendarAlarm;
    Calendar calenderMessage;

    int seq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //Set formatters
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("hh:mm", Locale.US);

        //Get GUI elements
        name = (EditText) findViewById(R.id.editTextName);
        phone = (EditText) findViewById(R.id.editTextPhone);
        type = (Spinner) findViewById(R.id.spinnerType);
        eventDate = (EditText) findViewById(R.id.editTextDate);
        alarmDate = (EditText) findViewById(R.id.editTextAlarmDate);
        alarmTime = (EditText) findViewById(R.id.editTextAlarmTime);
        isAlarm = (CheckBox) findViewById(R.id.checkBoxIsAlarm);
        isMessage = (CheckBox) findViewById(R.id.checkBoxIsMessage);
        message = (EditText) findViewById(R.id.editTextMessage);

        //Get calendar values from respective dialogs
        setDateField();
        setAlarmDateField();
        setAlarmTimeField();

        //Set calendars to default value
        calendarAlarm = Calendar.getInstance();
        calenderMessage = Calendar.getInstance();
        calenderMessage.setTimeInMillis(System.currentTimeMillis());
        calenderMessage.clear();

        //calendarAlarm.setTimeInMillis(System.currentTimeMillis());

        //Create or Open database
        db = openOrCreateDatabase("Events.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        //db.execSQL("CREATE TABLE IF NOT EXISTS events(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, phone VARCHAR, date VARCHAR, type VARCHAR, message VARCHAR);");

        //Enable or Diable pickers according to checkboxes
        isAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false)
                {
                    alarmDate.setEnabled(false);
                    alarmTime.setEnabled(false);
                }
                else {
                    alarmDate.setEnabled(true);
                    alarmTime.setEnabled(true);
                }
            }
        });

        isMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false)
                {
                    message.setEnabled(false);
                }
                else {
                    message.setEnabled(true);
                }
            }
        });
        eventDate.setKeyListener(null);
        alarmDate.setKeyListener(null);
        alarmTime.setKeyListener(null);
    }

    //Set calendar values based on user selection
    private void setAlarmDateField() {
        alarmDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        alarmDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                alarmDate.setText(dateFormatter.format(newDate.getTime()));

                calendarAlarm.set(Calendar.MONTH, newDate.get(Calendar.MONTH));
                calendarAlarm.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH));
                calendarAlarm.set(Calendar.YEAR, newDate.get(Calendar.YEAR));

            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setAlarmTimeField() {
        alarmTime.setOnClickListener(this);
    }

    private void setDateField() {
        eventDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        eventDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                eventDate.setText(dateFormatter.format(newDate.getTime()));

                calenderMessage.set(Calendar.MONTH, monthOfYear);
                calenderMessage.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calenderMessage.set(Calendar.YEAR, year);
                calenderMessage.set(Calendar.HOUR, 0);
                calenderMessage.set(Calendar.MINUTE, 0);
                calenderMessage.set(Calendar.MILLISECOND, 0);

            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
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
        else if(id == R.id.save) {

            //Validate all fields before saving
            if(!isValidName(name.getText().toString())) {
                name.setError("Invalid Name");
                isValidationComplete = false;
            }

            if(!isValidPhone(phone.getText().toString())){
                phone.setError("Invalid Phone");
                isValidationComplete = false;
            }

            if(!isValidEventDate(eventDate.getText().toString())) {
                eventDate.setError("Invalid Date");
                isValidationComplete = false;
            }

            if(isAlarm.isChecked()){
                if(!isValidAlarmDate(alarmDate.getText().toString())) {
                    alarmDate.setError("Invalid Date");
                    isValidationComplete = false;
                }

                if(!isValidAlarmTime(alarmTime.getText().toString())) {
                    alarmTime.setError("Invalid Time");
                    isValidationComplete = false;
                }
            }

            if(isMessage.isChecked()){
                if(!isValidMessage(message.getText().toString())) {
                    message.setError("Invalid Message");
                    isValidationComplete = false;
                }
            }

            //Save to db if validation completed
            if(isValidationComplete) {
                save();
                Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class); //Go back to home
                startActivity(nextScreen);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == eventDate) {
            eventDatePickerDialog.show();
        }
        else if(view == alarmDate) {
            alarmDatePickerDialog.show();
        }
        else if(view == alarmTime) {
            // TODO Auto-generated method stub
            Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mCurrentTime.get(Calendar.MINUTE);

            calendarAlarm.set(Calendar.HOUR, hour);
            calendarAlarm.set(Calendar.MINUTE, minute);

            alarmTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    alarmTime.setText( selectedHour + ":" + selectedMinute);

                    calendarAlarm.set(Calendar.HOUR, selectedHour);
                    calendarAlarm.set(Calendar.MINUTE, selectedMinute);
                    calendarAlarm.set(Calendar.MILLISECOND, 0);

                }
            }, hour, minute, true);//Yes 24 hour time
            alarmTimePickerDialog.show();

        }
    }

    //Write to database
    private void save(){
        try {

            db.execSQL("INSERT INTO events (name, phone, date, type, isAlarm, isMessage, message) VALUES('" + name.getText().toString() + "', '"+ phone.getText().toString() + "', '" + eventDate.getText().toString() +"', '"+ type.getSelectedItem().toString() + "', '"+ isAlarm.isChecked()+ "', '" + isMessage.isChecked() + "', '" + message.getText().toString() +"');");

            Cursor c=db.rawQuery("SELECT last_insert_rowid()", null);
            if(c.moveToNext()) {
                seq = Integer.parseInt(c.getString(0));
            }
            db.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Set the alarm according to date and time selected by user
        if(isAlarm.isChecked()){
            Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.setData(Uri.parse("alarm://" + seq));
            intent.setAction(String.valueOf(seq));

            intent.putExtra("name", name.getText().toString());
            intent.putExtra("type", type.getSelectedItem().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.getTimeInMillis(), pendingIntent);
        }

        //Set the message to be sent on 12:00AM or 00h00 of event date
        if(isMessage.isChecked()) {
            Intent intent = new Intent(getBaseContext(), MessageReceiver.class);
            intent.setData(Uri.parse("message://" + seq));
            intent.setAction(String.valueOf(seq));

            intent.putExtra("phone", phone.getText().toString());
            intent.putExtra("msg", message.getText().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calenderMessage.getTimeInMillis(), pendingIntent);
        }
    }

    //Validator methods
    private boolean isValidName(String name) {
        if (name == null || name.isEmpty() || name.trim().length()==0) {
            return false;
        }
        return true;
    }

    private boolean isValidPhone(String phone) {
        String PHONE_PATTERN = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean isValidEventDate(String date) {
        if (date == null || date.isEmpty() || date.trim().length()==0 || date.equals("")) {
            return false;
        }
        else
            return true;
    }

    private boolean isValidAlarmDate(String date) {
        if (date == null || date.isEmpty() || date.trim().length()==0 || date.equals("")) {
            return false;
        }
        else
            return true;
    }

    private boolean isValidAlarmTime(String time) {
        if (time == null || time.isEmpty() || time.trim().length()==0 || time.equals("")) {
            return false;
        }
        return true;
    }

    private boolean isValidMessage(String msg) {
        if (msg == null || msg.isEmpty() || msg.trim().length()==0 || msg.equals("")) {
            return false;
        }
        return true;
    }

}


