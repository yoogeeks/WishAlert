package uha.ensisa.android.wishalert;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Similar to AddEventActivity, only updates previous db entry based on id passed
public class EditEventActivity extends ActionBarActivity implements View.OnClickListener{

    private DatePickerDialog eventDatePickerDialog;

    private DatePickerDialog alarmDatePickerDialog;
    private TimePickerDialog alarmTimePickerDialog;

    SQLiteDatabase db;

    private EditText name;
    private EditText phone;
    private Spinner type;
    private EditText eventDate;
    private EditText alarmDate;
    private EditText alarmTime;
    private CheckBox isAlarm;
    private CheckBox isMessage;
    private EditText message;

    private Boolean isValidationComplete = true;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    Calendar calendarAlarm;
    Calendar calenderMessage;

    String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();

        //Get id of the field to be updated
        eventId = intent.getStringExtra("id");

        db = openOrCreateDatabase("Events.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor c=db.rawQuery("SELECT * FROM events WHERE id=" + eventId , null);
        Event event = new Event();
        if(c.moveToNext())
        {
            event.setId(Integer.parseInt(c.getString(0)));
            event.setName(c.getString(1));
            event.setPhone(c.getString(2));
            //event.setId(Integer.parseInt(c.getString()));
            event.setDate(c.getString(3));
            event.setType(c.getString(4));
            event.setIsAlarm(Boolean.valueOf(c.getString(5)));
            event.setIsMessage(Boolean.valueOf(c.getString(6)));
            event.setMessage(c.getString(7));
        }

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("hh:mm", Locale.US);

        name = (EditText) findViewById(R.id.editTextName);
        phone = (EditText) findViewById(R.id.editTextPhone);
        type = (Spinner) findViewById(R.id.spinnerType);
        eventDate = (EditText) findViewById(R.id.editTextDate);
        alarmDate = (EditText) findViewById(R.id.editTextAlarmDate);
        alarmTime = (EditText) findViewById(R.id.editTextAlarmTime);
        isAlarm = (CheckBox) findViewById(R.id.checkBoxIsAlarm);
        isMessage = (CheckBox) findViewById(R.id.checkBoxIsMessage);
        message = (EditText) findViewById(R.id.editTextMessage);

        name.setText(event.getName());
        phone.setText(event.getPhone());
        eventDate.setText(event.getDate());
        isAlarm.setChecked(event.getIsAlarm());
        isMessage.setChecked(event.getIsMessage());

        if(!(event.getMessage().isEmpty())){
            message.setText(event.getMessage());
        }

        setDateField();
        setAlarmDateField();
        setAlarmTimeField();

        calendarAlarm = Calendar.getInstance(TimeZone.getDefault());
        calendarAlarm.setTimeInMillis(System.currentTimeMillis());
        calendarAlarm.clear();

        calenderMessage = Calendar.getInstance();
        calenderMessage.setTimeInMillis(System.currentTimeMillis());
        calenderMessage.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }


    private void setAlarmDateField() {
        alarmDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        alarmDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
        eventDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                eventDate.setText(dateFormatter.format(newDate.getTime()));
                //calenderMessage.setTimeInMillis(newDate.getTimeInMillis());
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

            if(isValidationComplete) {
                save();
                Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(nextScreen);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //Update values
    public void save() {
        try {
            db.execSQL("UPDATE events SET name='"+ name.getText().toString() + "', phone='" + phone.getText().toString() +"', date='"+ eventDate.getText().toString() + "', type='" + type.getSelectedItem().toString() + "', isAlarm='" + isAlarm.isChecked() + "', isMessage='" + isMessage.isChecked() + "', message='" + message.getText().toString() +"' WHERE id=" + eventId);
            db.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.setData(Uri.parse("alarm://" + eventId));
            intent.setAction(String.valueOf(eventId));

            intent.putExtra("name", name.getText().toString());
            intent.putExtra("type", type.getSelectedItem().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            if(isAlarm.isChecked()){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarAlarm.getTimeInMillis(), pendingIntent);
            }
        }catch (Exception e){

        }

        try{
            Intent intent = new Intent(getBaseContext(), MessageReceiver.class);
            intent.setData(Uri.parse("message://" + eventId));
            intent.setAction(String.valueOf(eventId));

            intent.putExtra("phone", phone.getText().toString());
            intent.putExtra("msg", message.getText().toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if(calenderMessage.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                calenderMessage.set(Calendar.YEAR, Calendar.getInstance().YEAR +1);
            }

            alarmManager.cancel(pendingIntent);
            if(isMessage.isChecked()){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calenderMessage.getTimeInMillis(), pendingIntent);
            }
        }
        catch (Exception e){

        }
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
