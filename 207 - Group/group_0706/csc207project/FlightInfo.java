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
 * An activity which displays information for a flight. Allows the displayed flight to be booked
 * or removed from bookings.
 */
public class FlightInfo extends AppCompatActivity {


    private FlightApp flightApp;
    private Client user;
    private Flight flight;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);

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
        getMenuInflater().inflate(R.menu.menu_flight_info, menu);
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
     * Adds the flight to user's bookings if the flight has seats available.
     *
     * @param view The button for booking flights.
     */
    public void bookFlight(View view){
        if (this.flight.getNumSeats() > 0){
            List<Booking> bookings = this.user.getBooking();
            if(bookings == null){
                bookings = new ArrayList<>();
            }
            bookings.add
                    (new Booking(this.user.getEmail(), new Itinerary(this.flight)));
            this.user.setBooking(bookings);
            this.flight.setNumSeats(this.flight.getNumSeats() - 1);

            HashMap<Integer, Flight> flightsMap = this.flightApp.getFlightHashMap();
            flightsMap.put(new Integer(this.flight.getFlightNumber()), this.flight);

            HashMap<String, Client> clientMap = this.flightApp.getUserHashMap();
            clientMap.put(this.user.getEmail(), this.user);

            this.flightApp.setFlightHashMap(flightsMap);
            this.flightApp.setUserHashMap(clientMap);

            this.flightApp.savePersistentData();

            TextView numSeats = (TextView) findViewById(R.id.textViewNumSeats);
            numSeats.setText(String.valueOf(flight.getNumSeats()));
            Toast.makeText(getApplicationContext(),
                    "Flight successfully booked.",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Error: The flight is full.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Removes the booking for this flight from user if the user has the booking.
     *
     * @param view
     */
    public void cancelBooking(View view){
        int indexOfBooking = -1;
        Itinerary flights;
        List<Booking> bookings = this.user.getBooking();
        if (!(bookings == null)) {
            for (int index = 0; index < this.user.getBooking().size(); index++) {

                flights = bookings.get(index).getFlights();
                if (flights.getFlights().size() == 1 && this.flight.getFlightNumber()
                        == flights.getFlights().get(0).getFlightNumber()) {
                    indexOfBooking = index;
                }
            }

            //Flight found in bookings
            if (indexOfBooking > -1) {
                bookings.remove(indexOfBooking);
                //Update user information
                this.user.setBooking(bookings);
                HashMap<String, Client> map = this.flightApp.getUserHashMap();
                map.put(user.getEmail(), this.user);

                //Update flight seatings
                this.flight.setNumSeats(this.flight.getNumSeats() + 1);
                HashMap<Integer, Flight> flightsMap = this.flightApp.getFlightHashMap();
                flightsMap.put(new Integer(this.flight.getFlightNumber()), this.flight);

                //Update the hashmaps in flightapp.
                this.flightApp.setFlightHashMap(flightsMap);
                this.flightApp.setUserHashMap(map);

                this.flightApp.savePersistentData();

                TextView numSeats = (TextView) findViewById(R.id.textViewNumSeats);
                numSeats.setText(String.valueOf(flight.getNumSeats()));
                Toast.makeText(getApplicationContext(),
                        "Booking successfully removed.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Booking not found.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Booking not found.",
                    Toast.LENGTH_SHORT).show();
        }
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
