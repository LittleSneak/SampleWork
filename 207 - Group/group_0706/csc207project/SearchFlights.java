package group_0706.csc207project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import flightapp.*;

/**
 * An activity where users can enter search parameters for searching for a flight.
 */
public class SearchFlights extends AppCompatActivity {

    private FlightApp flightApp;
    private Client user;
    private boolean isAdmin;

    private int day;
    private int month;
    private int year;
    static final int DIALOG_ID = 0;

    private TextView dateDisplay;
    private Button pickDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.isAdmin = (Boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }

        //Sets up the date picker with the current date.
        dateDisplay = (TextView) findViewById(R.id.textViewSelectedDate);
        pickDate = (Button) findViewById(R.id.buttonChooseDate);

        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        final Calendar calendar = Calendar.getInstance();
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);

        updateDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_flights, menu);
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the text view to have the date
     */
    public void updateDisplay(){
        int strMonth = this.month + 1;
        this.dateDisplay.setText("" + strMonth + "-" + this.day + "-" + this.year);
    }

    /**
     * Obtains the date from the date picker dialog and sets the fields.
     */
    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int givenYear,
                                      int givenMonth, int givenDay) {
                    year = givenYear;
                    month = givenMonth;
                    day = givenDay;
                    updateDisplay();
                }
            };

    /**
     * Returns a date picker dialog if an appropriate ID is given.
     *
     * @param id The ID for the date dialog.
     * @return The date picker dialog.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == this.DIALOG_ID){
            return new DatePickerDialog(this,
                    dateSetListener,
                    year, month, day);
        }
        return null;
    }
    /**
     * Goes to the SearchFlightResults activity and gives it the flightApp, user, true if the user
     * is and admin, date, origin, and destination.
     *
     * @param view The search button.
     */
    public void search(View view){
        //Obtain all the info from the text fields

        EditText origin = (EditText) findViewById(R.id.editTextOrigin2);
        String newOrigin = origin.getText().toString();

        EditText destination = (EditText) findViewById(R.id.editTextDestination2);
        String newDestination= destination.getText().toString();

        String newDate = String.valueOf(this.year) + "-";
        int newMonth = this.month + 1;

        if(newMonth < 10){
            newDate = newDate + "0" + newMonth;
        }
        else{
            newDate = newDate + newMonth;
        }

        newDate = newDate + "-";
        if (this.day < 10){
            newDate = newDate + "0" + this.day;
        }
        else{
            newDate = newDate + this.day;
        }

        //Put the stuff into the next activity
        Intent intent = new Intent(this, SearchFlightResults.class);

        intent.putExtra("Date", newDate);
        intent.putExtra("Origin", newOrigin);
        intent.putExtra("Destination", newDestination);
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", this.isAdmin);

        startActivityForResult(intent, 1);
    }

    /**
     * Goes to the previous activity and gives it flightApp and User when back is clicked.
     */
    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("FlightApp", this.flightApp);
        returnIntent.putExtra("User", this.user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Obtains a FlightApp and User from an activity this activity started.
     *
     * @param requestCode The number which represents the activity call
     * @param resultCode The number which shows if the call returned a result
     * @param data The intent from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");

                if(this.isAdmin){
                    this.user = (Admin) data.getSerializableExtra("User");
                }
                else {
                    this.user = (Client) data.getSerializableExtra("User");
                }
            }
        }
    }
}
