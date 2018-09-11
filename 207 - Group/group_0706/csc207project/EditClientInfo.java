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

public class EditClientInfo extends AppCompatActivity implements View.OnClickListener{


    private FlightApp flightApp;
    private Admin user;
    private List<Client> clients;
    private String purpose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client_info);

        Intent intent = getIntent();
        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.user = (Admin) intent.getSerializableExtra("User");
        this.clients = flightApp.getClientList();
        this.purpose = (String) intent.getSerializableExtra("Purpose");

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn;
        int id = 0;
        for(Client client : this.clients) {
            //The admin cannot edit their own information or book for themselves here.
            if(!(client.getEmail().equals(this.user.getEmail()))) {
                btn = new Button(this);
                btn.setId(id);
                btn.setText(client.getEmail());
                btn.setOnClickListener(this);

                ll.addView(btn, lp);

                id = id + 1;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_client_info, menu);
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

    public void onClick(View view){
        Intent intent;

        Button btn = (Button) view;
        String email = btn.getText().toString();

        int index = 0;
        while (!this.clients.get(index).getEmail().equals(email)){
            index = index + 1;
        }

        if (this.purpose.equals("Edit client info")) {
            intent = new Intent(this, EditPersonalInfo.class);
            intent.putExtra("User", this.clients.get(index));
            intent.putExtra("FlightApp", this.flightApp);
            intent.putExtra("isAdmin", this.clients.get(index) instanceof Admin);
        }

        else if(this.purpose.equals("Book for client")){

            intent = new Intent(this, ItineraryOrFlight.class);
            intent.putExtra("FlightApp", this.flightApp);
            intent.putExtra("User", this.clients.get(index));
            intent.putExtra("isAdmin", this.clients.get(index) instanceof Admin);
        }

        else{
            intent = new Intent(this, EditBookings.class);
            intent.putExtra("User", this.clients.get(index));
            intent.putExtra("FlightApp", this.flightApp);
            intent.putExtra("isAdmin", this.clients.get(index) instanceof Admin);
        }

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
            }
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
}
