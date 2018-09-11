package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.*;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import flightapp.*;


/**
 * An activity where a user can see all their bookings.
 */
//Implements OnClickListener so we can set onClicks to buttons
public class EditBookings extends AppCompatActivity implements View.OnClickListener {

    private FlightApp flightApp;
    private Client user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bookings);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.isAdmin = (Boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }

        List<Booking> booking = user.getBooking();
        if (booking == null){
            booking = new ArrayList<>();
        }

        //Add buttons to this layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        Itinerary flights;
        Button b;
        int id = 0;

        //Go through all the bookings in the user and make a button for each booking then
        //add the button onto layout
        for (int  x = 0; x < booking.size(); x = x + 1){
            flights = booking.get(x).getFlights();
            b = new Button(this);
            b.setText(x + ": " + flights.getFlights().get(0).getOrigin() + " to "
                    + flights.getDestination());
            b.setId(id);


            //Sets the button's OnClickListener to this class' onClick since this class implements
            //onClickListener
            b.setOnClickListener(this);

            ll.addView(b, lp);
            id = id + 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_bookings, menu);
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
     * Goes to the ItineraryActivity activity and gives it the current user, flightApp, whether
     * user is an admin or not, and the itinerary being viewed.
     *
     * @param view The button that was clicked in the scrollview.
     */
    @Override
    public void onClick(View view){
        Button btn = (Button) view;

        Intent intent = new Intent(this, ItineraryActivity.class);

        //The buttons are made so that the first character in the button's text is the index of
        //the booking which has the Itinerary. Need to make flightapp serializable.
        intent.putExtra("Itinerary", this.user.getBooking().get
                (Character.getNumericValue(btn.getText().toString().charAt(0))).getFlights());
        intent.putExtra("User", this.user);
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("isAdmin", this.isAdmin);

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
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
                if (this.isAdmin) {
                    this.user = (Admin) data.getSerializableExtra("User");
                }

                else{
                    this.user = (Client) data.getSerializableExtra("User");
                }

                //Remake the button list.
                LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
                LayoutParams lp =
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                ll.removeAllViews();

                Itinerary flights;
                Button b;
                int id = 0;

                List<Booking> booking = user.getBooking();
                if (booking == null){
                    booking = new ArrayList<>();
                }
                //Go through all the bookings in the user and make a button for each booking then
                //add the button onto layout
                for (int  x = 0; x < booking.size(); x = x + 1){
                    flights = booking.get(x).getFlights();
                    b = new Button(this);
                    b.setText(x + ": " + flights.getFlights().get(0).getOrigin() + " to "
                            + flights.getDestination());
                    b.setId(id);

                    b.setOnClickListener(this);

                    ll.addView(b, lp);
                    id = id + 1;
                }
            }
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
