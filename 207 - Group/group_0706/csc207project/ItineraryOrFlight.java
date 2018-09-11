package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import flightapp.*;

/**
 * An activity where an admin can choose to book an itinerary or flight for a client.
 */
public class ItineraryOrFlight extends AppCompatActivity {

    private FlightApp flightApp;
    private Client user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_or_flight);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.isAdmin = (boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_itinerary_or_flight, menu);
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
     * Goes to the SearchFlights activity and gives it the user, flightApp, and true if this user
     * is an admin.
     *
     * @param view The button for search flights
     */
    public void bookFlight(View view){
        Intent intent = new Intent(this, SearchFlights.class);

        intent.putExtra("User", this.user);
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("isAdmin", this.isAdmin);

        startActivityForResult(intent, 1);
    }

    /**
     * Goes to the SearchItineraries activity and gives it the user, flightApp, and true if this
     * user is an admin.
     *
     * @param view The button for search itineraries.
     */
    public void bookItinerary(View view){
        Intent intent = new Intent(this, SearchItineraries.class);

        intent.putExtra("User", this.user);
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("isAdmin", this.isAdmin);

        startActivityForResult(intent, 2);
    }

    /**
     * Goes to the previous activity and gives it the flightApp and user when back is clicked.
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
        if (requestCode == 1 || requestCode == 2) {
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
