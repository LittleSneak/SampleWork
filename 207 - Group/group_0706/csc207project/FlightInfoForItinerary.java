package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import flightapp.*;

//This will be called by an Itinerary Info activity. That activity will have a scrollview of
//buttons for each flight in the itinerary.

/**
 * An activity which displays information for a flight.
 */
public class FlightInfoForItinerary extends AppCompatActivity {


    private FlightApp flightApp;
    private Client user;
    private Flight flight;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info_for_itinerary);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");

        this.isAdmin = (boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }
        this.flight = (Flight) intent.getSerializableExtra("Flight");

        //Get the newest version of this flight with the correct numSeats
        this.flight = this.flightApp.getFlightHashMap().get(this.flight.getFlightNumber());

        //Begin populating the information
        TextView flightNumber = (TextView) findViewById(R.id.textCurrentUser);
        flightNumber.setText(String.valueOf(flight.getFlightNumber()));

        TextView origin = (TextView) findViewById(R.id.textOrigin2);
        origin.setText(flight.getOrigin());

        TextView destination = (TextView) findViewById(R.id.textDestination2);
        destination.setText(flight.getDestination());

        TextView arrival = (TextView) findViewById(R.id.textArrival2);
        arrival.setText(flight.getArrivalString());

        TextView departure = (TextView) findViewById(R.id.textDeparture2);
        departure.setText(flight.getDepartureString());

        TextView price = (TextView) findViewById(R.id.textPrice2);
        price.setText(String.valueOf(flight.getPrice()));

        TextView airline = (TextView) findViewById(R.id.textAirline2);
        airline.setText(flight.getAirline());

        TextView numSeats = (TextView) findViewById(R.id.textViewNumSeats);
        numSeats.setText(String.valueOf(flight.getNumSeats()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flight_info_for_itinerary, menu);
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
     * Returns flightapp to the previous activity when back is pressed.
     *
     */
    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("FlightApp", this.flightApp);
        returnIntent.putExtra("User", this.user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
