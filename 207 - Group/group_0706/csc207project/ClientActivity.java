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
 * An activity for clients
 */
public class ClientActivity extends AppCompatActivity {

    private FlightApp flightApp;
    private Client user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Intent intent = getIntent();
        this.flightApp = (FlightApp) intent.getSerializableExtra("manager");
        this.user = (Client) intent.getSerializableExtra("activeClient");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
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
     * Goes to the EditPersonalInfo activity and gives it the flightapp and user using this
     * activity. Also a boolean to tell if the user is an admin.
     *
     * @param view The button for edit personal info
     */
    public void editInfo(View view){
        //Move onto the activity for editting info
        Intent intent = new Intent(this, EditPersonalInfo.class);

        //Give it the flightApp and user
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", false);

        startActivityForResult(intent, 1);
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
        if (requestCode == 1 || requestCode == 2 || requestCode == 3 || requestCode == 4) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
                this.user = (Client) data.getSerializableExtra("User");
            }
        }
    }

    /**
     * Goes to the SearchFlights activity and gives it the flightapp and user using this
     * activity. Also a boolean to tell if the user is an admin.
     *
     * @param view The button for search flights
     */
    public void searchFlights(View view){
        //Move onto the activity for searching
        Intent intent = new Intent(this, SearchFlights.class);

        //Give it the flightApp and user
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", false);

        //Start the activity
        startActivityForResult(intent, 2);
    }

    /**
     * Goes to the SearchItineraries activity and gives it the flightapp and user using this
     * activity. Also a boolean to tell if the user is an admin.
     *
     * @param view The button for search itineraries
     */
    public void searchItineraries(View view){
        //Move onto the activity for searching
        Intent intent = new Intent(this, SearchItineraries.class);

        //Give it the flightApp and user
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", false);

        //Start the activity
        startActivityForResult(intent, 3);
    }

    /**
     * Goes to the EditBookings activity and gives it the flightapp and user using this
     * activity. Also a boolean to tell if the user is an admin.
     *
     * @param view The button for editing bookings.
     */
    public void editBookings(View view){
        Intent intent = new Intent(this, EditBookings.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", false);

        startActivityForResult(intent, 4);
    }

    /**
     * Returns flightapp to the previous activity when back is pressed.
     *
     */
    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("FlightApp", this.flightApp);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
