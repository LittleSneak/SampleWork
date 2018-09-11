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

import java.util.List;

import flightapp.*;

/**
 * An activity which displays all the flights in the system.
 */
public class EditFlightInfo extends AppCompatActivity implements View.OnClickListener{

    private FlightApp flightApp;
    private Client user;
    private List<Flight> flights;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight_info);

        Intent intent = getIntent();
        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.user = (Client) intent.getSerializableExtra("User");
        this.flights = flightApp.getFlightList();

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn;
        int id = 0;
        for(Flight flight : this.flights) {
            btn = new Button(this);
            btn.setId(id);
            btn.setText(String.valueOf(flight.getFlightNumber()));
            btn.setOnClickListener(this);

            ll.addView(btn, lp);

            id = id + 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_flight_info, menu);
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
     * Goes to the EditFlight activity and gives it a flight, user, and flightApp.
     *
     * @param view The button that was clicked in the scrollview.
     */
    public void onClick(View view){
        Intent intent = new Intent(this, EditFlight.class);
        Button btn = (Button) view;
        String flightNum = btn.getText().toString();

        int index = 0;
        while (!(this.flights.get(index).getFlightNumber() == Integer.parseInt(flightNum))){
            index = index + 1;
        }

        intent.putExtra("Flight", this.flights.get(index));
        intent.putExtra("FlightApp", this.flightApp);
        intent.putExtra("User", this.user);
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
                this.flights = flightApp.getFlightList();
                this.user = (Admin) data.getSerializableExtra("User");
            }

            LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Button btn;
            int id = 0;
            this.flights = flightApp.getFlightList();
            for(Flight flight : this.flights) {
                btn = new Button(this);
                btn.setId(id);
                btn.setText(String.valueOf(flight.getFlightNumber()));
                btn.setOnClickListener(this);

                ll.addView(btn, lp);

                id = id + 1;
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
