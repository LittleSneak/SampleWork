package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import flightapp.*;

public class EdittableFlightInfo extends AppCompatActivity {

    private FlightApp flightApp;
    private Admin user;
    private Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittable_flight_info);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.user = (Admin) intent.getSerializableExtra("User");
        this.flight = (Flight) intent.getSerializableExtra("Flight");

        EditText flightNum = (EditText) findViewById(R.id.editTextFlightNumber);
        flightNum.setText(flight.getFlightNumber());

        EditText origin = (EditText) findViewById(R.id.editTextOrigin2);
        origin.setText(flight.getOrigin());

        EditText destination = (EditText) findViewById(R.id.editTextDestination2);
        destination.setText(flight.getDestination());

        EditText departure = (EditText) findViewById(R.id.editTextDeparture);
        departure.setText(flight.getDepartureString());

        EditText arrival = (EditText) findViewById(R.id.editTextArrival);
        arrival.setText(flight.getArrivalString());

        EditText airline = (EditText) findViewById(R.id.editTextAirline);
        airline.setText(flight.getAirline());

        EditText price = (EditText) findViewById(R.id.editTextPrice);
        price.setText(String.valueOf(flight.getPrice()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edittable_flight_info, menu);
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

    public void save(){

        EditText flightNum = (EditText) findViewById(R.id.editTextFlightNumber);
        int newFlightNum = Integer.parseInt(flightNum.getText().toString());

        EditText origin = (EditText) findViewById(R.id.editTextOrigin2);
        String newOrigin = origin.getText().toString();

        EditText destination = (EditText) findViewById(R.id.editTextDestination2);
        String newDestination = destination.getText().toString();

        EditText departure = (EditText) findViewById(R.id.editTextDeparture);
        String newDeparture = departure.getText().toString();

        EditText arrival = (EditText) findViewById(R.id.editTextArrival);
        String newArrival = arrival.getText().toString();

        EditText airline = (EditText) findViewById(R.id.editTextAirline);
        String newAirline = airline.getText().toString();

        EditText price = (EditText) findViewById(R.id.editTextPrice);
        double newPrice = Double.parseDouble(price.getText().toString());

        this.flight.setFlightNumber(newFlightNum);
        this.flight.setOrigin(newOrigin);
        this.flight.setDestination(newDestination);
        this.flight.setDeparture(newDeparture);
        this.flight.setArrival(newArrival);
        this.flight.setAirline(newAirline);
        this.flight.setPrice(newPrice);

        this.flightApp.savePersistentData();

        Toast.makeText(getApplicationContext(),
                "Saved!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("FlightApp", this.flightApp);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
