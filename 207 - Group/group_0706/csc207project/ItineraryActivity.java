package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import flightapp.*;


/**
 * An activity that displays all the flights in a given itinerary. This itinerary can be booked.
 */
public class ItineraryActivity extends AppCompatActivity implements View.OnClickListener{

    private FlightApp flightApp;
    private Client user;
    private Itinerary flights;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.isAdmin = (boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }

        this.flights = (Itinerary) intent.getSerializableExtra("Itinerary");

        //Obtain the list of the newest flights from flightApp with updated seat information.
        List<Flight> newFlights = new ArrayList<>();
        HashMap<Integer, Flight> flightMap = this.flightApp.getFlightHashMap();

        //Add buttons to this layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn;
        int id = 0;
        for (Flight flight : flights.getFlights()){
            newFlights.add(flightMap.get(flight.getFlightNumber()));
            btn = new Button(this);
            btn.setId(id);
            btn.setText(flight.getFlightNumber() + " " + flight.getOrigin()
                    + " to " + flight.getDestination());

            //Sets the button's OnClickListener to this class' onClick since this class implements
            //onClickListener
            btn.setOnClickListener(this);

            ll.addView(btn, lp);
            id = id + 1;
        }
        this.flights.setFlights(newFlights);
    }
    //   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_itinerary, menu);
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
     * Goes to the FlightInfo activity for the flight which corresponds to the button clicked.
     *
     * @param view The button that was clicked
     */
    public void onClick(View view){
        Intent intent = new Intent(this, FlightInfoForItinerary.class);
        Button btn = (Button) view;
        int flightNum = Integer.parseInt(btn.getText().toString().split(" ")[0]);

        //Find the flight
        Flight theFlight = null;
        for (Flight flight : this.flights.getFlights()) {
            if(flight.getFlightNumber() == flightNum) {
                theFlight = flight;
            }
        }

        //Give the stuff to the next activity
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("Flight", theFlight);
        intent.putExtra("isAdmin", this.isAdmin);

        startActivityForResult(intent, 1);
    }

    /**
     * Removes a booking of this itinerary from this user if they have the booking.
     *
     * @param view The button for removing bookings.
     */
    public void removeBooking(View view) {
        int count = 0;
        List<Booking> bookings = this.user.getBooking();
        if(bookings == null){
            bookings = new ArrayList<>();
        }
        //Check if the user has the booking.
        while (count < bookings.size() && !this.flights.equals(bookings.get(count).getFlights())) {
            count = count + 1;
        }

        //If there is a booking to remove
        Flight f;
        if (count < this.user.getBooking().size()) {
            //Remove the booking with the given Itinerary
            bookings.remove(count);
            HashMap<Integer, Flight> flightsMap = this.flightApp.getFlightHashMap();

            //A new seat is now available for each flight.
            for (int x = 0; x < this.flights.getFlights().size(); x++) {
                f = this.flights.getFlights().get(x);
                f.setNumSeats(f.getNumSeats() + 1);
                //Update the flightApp's hashmap
                flightsMap.put(new Integer(f.getFlightNumber()), f);
            }

            this.user.setBooking(bookings);

            HashMap<String, Client> clientMap = this.flightApp.getUserHashMap();
            clientMap.put(this.user.getEmail(), this.user);

            this.flightApp.setFlightHashMap(flightsMap);
            this.flightApp.setUserHashMap(clientMap);

            this.flightApp.savePersistentData();
            Toast.makeText(getApplicationContext(),
                    "Booking removed.",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Booking not found.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Books this itinerary for this user if there are seats available.
     *
     * @param view The button for booking this itinerary.
     */
    public void bookFlight(View view){
        int seats;
        //Check if all the flights have an available spot
        boolean allAvailable = true;
        for (int x = 0; x < this.flights.getFlights().size(); x++){
            seats = this.flights.getFlights().get(x).getNumSeats();
            if (seats == 0){
                allAvailable = false;
            }
        }

        if (allAvailable) {
            //Book the flight
            List<Booking> bookings = this.user.getBooking();
            List<Flight> listOfFlights= new ArrayList<>();
            if (bookings == null){
                bookings = new ArrayList<>();
            }
            HashMap<Integer, Flight> flightsMap = this.flightApp.getFlightHashMap();

            Flight f;
            //Decrease the seats available in each flight by 1
            for (int x = 0; x < this.flights.getFlights().size(); x++) {
                f = this.flights.getFlights().get(x);
                f.setNumSeats(f.getNumSeats() - 1);
                //Update the flightApp's hashmap
                flightsMap.put(new Integer(f.getFlightNumber()), f);
                listOfFlights.add(f);
            }

            Itinerary itineraryToBook = new Itinerary(listOfFlights);
            bookings.add(new Booking(this.user.getEmail(), itineraryToBook));
            this.user.setBooking(bookings);

            HashMap<String, Client> clientMap = this.flightApp.getUserHashMap();
            clientMap.put(this.user.getEmail(), this.user);

            this.flightApp.setFlightHashMap(flightsMap);
            this.flightApp.setUserHashMap(clientMap);
            this.flightApp.savePersistentData();
            Toast.makeText(getApplicationContext(),
                    "Itinerary successfully booked.",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "A flight is over capacity.",
                    Toast.LENGTH_SHORT).show();
        }
    }

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

                if(this.isAdmin) {
                    this.user = (Admin) data.getSerializableExtra("User");
                }
                else {
                    this.user = (Client) data.getSerializableExtra("User");
                }
            }
        }
    }
}
