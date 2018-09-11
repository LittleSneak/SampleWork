package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import flightapp.*;

/**
 * An activity that displays the results of a search for itineraries.
 */
public class SearchResults extends AppCompatActivity implements View.OnClickListener{

    private FlightApp flightApp;
    private Client user;
    private String date;
    private String origin;
    private String destination;
    private List<Itinerary> results;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");

        this.isAdmin = (Boolean) intent.getSerializableExtra("isAdmin");

        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }

        this.date = (String) intent.getSerializableExtra("Date");
        this.origin = (String) intent.getSerializableExtra("Origin");
        this.destination = (String) intent.getSerializableExtra("Destination");
        this.results = new ArrayList<>();

        Set<Itinerary> setItinerary =
                this.flightApp.searchItineraries(this.date, this.origin, this.destination);

        this.results.addAll(setItinerary);

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn;
        int id = 0;

        for (Itinerary flights : results){
            btn = new Button(this);
            btn.setId(id);
            btn.setText(String.valueOf(id) + " : Total time: " + flights.calculateTravelTime()
                    + " Price : " + flights.getTotalPrice());

            //Sets the button's OnClickListener to this class' onClick since this class implements
            //onClickListener
            btn.setOnClickListener(this);

            ll.addView(btn, lp);
            id = id + 1;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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
     * Goes to the ItineraryActivity activity and gives it the flightApp, user, and itinerary. The
     * itinerary given will correspond with the button clicked.
     *
     * @param view The button clicked in the scrollview.
     */
    public void onClick(View view){
        Intent intent = new Intent(this, ItineraryActivity.class);
        //Find the itinerary that the button that was clicked represented
        Button btn = (Button) view;
        int indexOfItinerary = Integer.parseInt(btn.getText().toString().split(" ")[0]);
        Itinerary flights = this.results.get(indexOfItinerary);

        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
        intent.putExtra("Itinerary", flights);
        intent.putExtra("isAdmin", this.isAdmin);

        startActivityForResult(intent, 1);
    }

    /**
     * Changes the buttons so that their labels match itineraries in such a way that it is sorted by
     * cost.
     *
     * @param view The button for sorting by cost.
     */
    public void sortByCost(View view){
        this.results = this.flightApp.sortItinerariesByCost(this.results);

        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        //Grab all the buttons from the layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        for(int x = 0; x < layout.getChildCount(); x = x + 1){
            button = (Button) layout.getChildAt(x);
            buttons.add(button);
        }

        int index = 0;
        Itinerary flights;

        //Change the buttons to the appropriate new flights
        for (Button btn : buttons){
            //The first part of the text of the button indicates which button it is
            index = Integer.parseInt(btn.getText().toString().split(" ")[0]);
            flights = this.results.get(index);
            btn.setText(String.valueOf(index) + " : Total time: " + flights.calculateTravelTime()
                    + " Price : " + flights.getTotalPrice());
            btn.setOnClickListener(this);
        }

    }

    /**
     * Changes the buttons so that their labels match itineraries in such a way that it is sorted by
     * time.
     *
     * @param view The button for sorting by time.
     */
    public void sortByTime(View view){
        this.results = this.flightApp.sortItinerariesByTime(this.results);

        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        //Grab all the buttons from the layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        for(int x = 0; x < layout.getChildCount(); x = x + 1){
            button = (Button) layout.getChildAt(x);
            buttons.add(button);
        }

        int index = 0;
        Itinerary flights;

        //Change the buttons to the appropriate new flights
        for (Button btn : buttons){
            //The first part of the text of the button indicates which button it is
            index = Integer.parseInt(btn.getText().toString().split(" ")[0]);
            flights = this.results.get(index);
            btn.setText(String.valueOf(index) + " : Total time: " + flights.calculateTravelTime()
                    + " Price : " + flights.getTotalPrice());
            btn.setOnClickListener(this);
        }

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
