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
 * An activity for admins
 */
public class AdminActivity extends AppCompatActivity {

    private FlightApp flightApp;
    private Admin user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("manager");
        this.user = (Admin) intent.getSerializableExtra("activeAdmin");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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
        intent.putExtra("isAdmin", true);

        startActivityForResult(intent, 1);
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
        intent.putExtra("isAdmin", true);

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
        intent.putExtra("isAdmin", true);

        //Start the activity
        startActivityForResult(intent, 3);
    }

    /**
     * Goes to the UploadInfo activity and gives it the flightapp and user using this
     * activity. Also a boolean to tell if the user is an admin.
     *
     * @param view The button uploading information
     */
    public void uploadInfo(View view){
        Intent intent = new Intent(this, UploadInfo.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);

        startActivityForResult(intent, 4);
    }

    /**
     * Goes to the EditClientInfo activity and gives it the flightapp and user using this
     * activity. Gives the purpose for going to that activity so the activity can tell
     * what functionality to have.
     *
     * @param view The button for editing client info.
     */
    public void editClientInfo(View view){
        Intent intent = new Intent(this, EditClientInfo.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("Purpose", "Edit client info");

        startActivityForResult(intent, 5);
    }

    /**
     * Goes to the EditFlightInfo activity and gives it the flightapp and user using this
     * activity.
     *
     * @param view The button for editing flight info.
     */
    public void editFlightInfo(View view){
        Intent intent = new Intent(this, EditFlightInfo.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);

        startActivityForResult(intent, 6);
    }

    /**
     * Goes to the EditClientInfo activity and gives it the flightapp and user using this
     * activity. Gives the purpose for going to that activity so the activity can tell
     * what functionality to have.
     *
     * @param view The button for booking for a client.
     */
    public void bookForClient(View view){
        Intent intent = new Intent(this, EditClientInfo.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("Purpose", "Book for client");

        startActivityForResult(intent, 7);
    }

    /**
     * Goes to the EditClientInfo activity and gives it the flightapp and user using this
     * activity. Gives the purpose for going to that activity so the activity can tell
     * what functionality to have.
     *
     * @param view The button for viewing client bookings.
     */
    public void viewClientBookings(View view){
        Intent intent = new Intent(this, EditClientInfo.class);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("Purpose", "View bookings");

        startActivityForResult(intent, 8);
    }

    /**
     * Goes to the EditBookings activity where the user can edit their bookings.
     *
     * @param view The button for editting personal bookings.
     */
    public void editPersonalBookings(View view){
        //Move onto the activity for searching
        Intent intent = new Intent(this, EditBookings.class);

        //Give it the flightApp and user
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("isAdmin", true);

        //Start the activity
        startActivityForResult(intent, 9);
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

    /**
     * Obtains a FlightApp and User from an activity this activity started.
     *
     * @param requestCode The number which represents the activity call
     * @param resultCode The number which shows if the call returned a result
     * @param data The intent from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //Editing admin info
        if (requestCode == 1 || requestCode == 2 || requestCode == 3 || requestCode == 4
                || requestCode == 6 || requestCode == 9) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
                this.user = (Admin) data.getSerializableExtra("User");
            }
        }

        //In this case, client info is being edited so we don't need to get the user back
        else if (requestCode == 5 || requestCode == 7 || requestCode == 8) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
            }
        }
    }
}
