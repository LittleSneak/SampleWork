package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.util.HashMap;

import flightapp.Admin;
import flightapp.Flight;
import flightapp.FlightApp;

//This will be called by an Itinerary Info activity. That activity will have a scrollview of
//buttons for each flight in the itinerary.

/**
 * An activity that shows all the information for a flight and allows it to be edited.
 */
public class EditFlight extends AppCompatActivity {

    private FlightApp flightApp;
    private Admin user;
    private Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.user = (Admin) intent.getSerializableExtra("User");
        this.flight = (Flight) intent.getSerializableExtra("Flight");

        //Begin populating the information
        EditText flightNumber = (EditText) findViewById(R.id.editFlightNum);
        flightNumber.setText(String.valueOf(flight.getFlightNumber()));

        EditText origin = (EditText) findViewById(R.id.editOrigin);
        origin.setText(flight.getOrigin());

        EditText destination = (EditText) findViewById(R.id.editDestination);
        destination.setText(flight.getDestination());

        EditText arrival = (EditText) findViewById(R.id.editArrival);
        arrival.setText(flight.getArrivalString());

        EditText departure = (EditText) findViewById(R.id.editDeparture);
        departure.setText(flight.getDepartureString());

        EditText price = (EditText) findViewById(R.id.editPrice);
        price.setText(String.valueOf(flight.getPrice()));

        EditText airline = (EditText) findViewById(R.id.editAirline);
        airline.setText(flight.getAirline());

        EditText seatNumber = (EditText) findViewById(R.id.editSeatNum);
        seatNumber.setText(String.valueOf(flight.getNumSeats()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_flight, menu);
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
     * Updates flightApp and the database with the new information for flight.
     *
     * @param view
     */
    public void saveChanges(View view){
        int oldNum = this.flight.getFlightNumber();
        EditText flightNumber = (EditText) findViewById(R.id.editFlightNum);
        flight.setFlightNumber(Integer.parseInt(flightNumber.getText().toString()));

        EditText origin = (EditText) findViewById(R.id.editOrigin);
        flight.setOrigin(origin.getText().toString());

        EditText destination = (EditText) findViewById(R.id.editDestination);
        flight.setDestination(destination.getText().toString());

        EditText arrival = (EditText) findViewById(R.id.editArrival);
        flight.setArrival(arrival.getText().toString());

        EditText departure = (EditText) findViewById(R.id.editDeparture);
        flight.setDeparture(departure.getText().toString());

        EditText price = (EditText) findViewById(R.id.editPrice);
        flight.setPrice(Double.parseDouble(price.getText().toString()));

        EditText airline = (EditText) findViewById(R.id.editAirline);
        flight.setAirline(airline.getText().toString());

        EditText seatNumber = (EditText) findViewById(R.id.editSeatNum);
        flight.setNumSeats(Integer.parseInt(seatNumber.getText().toString()));

        HashMap<Integer, Flight> flights = this.flightApp.getFlightHashMap();

        flights.put(new Integer(flight.getFlightNumber()), flight);

        //If the flight number has changed, update the hashmap by removing the old flight.
        if(oldNum != this.flight.getFlightNumber()){
            flights.remove(new Integer(oldNum));
        }
        this.flightApp.setFlightHashMap(flights);

        flightApp.savePersistentData();

        Toast.makeText(getApplicationContext(),
                "Saved",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns flightApp and user to the previous activity when back is clicked.
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
