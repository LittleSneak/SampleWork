package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import flightapp.*;

public class EditPersonalInfo extends AppCompatActivity {

    private FlightApp flightApp;
    private Client user;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);

        Intent intent = getIntent();

        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.isAdmin = (boolean) intent.getSerializableExtra("isAdmin");
        if(this.isAdmin){
            this.user = (Admin) intent.getSerializableExtra("User");
        }
        else {
            this.user = (Client) intent.getSerializableExtra("User");
        }

        //Get the newest information of this user
        HashMap<String, Client> users = this.flightApp.getUserHashMap();
        this.user = users.get(this.user.getEmail());

        //Begin filling in the information
        TextView username = (TextView) findViewById(R.id.textViewUsername);
        username.setText(user.getUsername());

        TextView password = (TextView) findViewById(R.id.textCurrentPass);
        password.setText(user.getPassword());

        EditText firstName = (EditText) findViewById(R.id.editTextFirstName);
        firstName.setText(user.getFirstName());

        EditText lastName = (EditText) findViewById(R.id.editTextLastName);
        lastName.setText(user.getLastName());

        EditText address = (EditText) findViewById(R.id.editTextAddress);
        address.setText(user.getAddress());

        TextView email = (TextView) findViewById(R.id.textViewEmail);
        email.setText(user.getEmail());

        EditText creditCardNumber = (EditText) findViewById(R.id.editTextCardNumber);
        creditCardNumber.setText(user.getCreditCardNumber());

        EditText expirationDate = (EditText) findViewById(R.id.editTextExpirationDate);
        expirationDate.setText(user.getExpirationDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_personal_info, menu);
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

    public void saveChanges(View view){
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();

        //Obtain all the information in the text fields
        EditText fName = (EditText) findViewById(R.id.editTextFirstName);
        String firstName = fName.getText().toString();

        EditText lName = (EditText) findViewById(R.id.editTextLastName);
        String lastName = lName.getText().toString();

        EditText address = (EditText) findViewById(R.id.editTextAddress);
        String newAddress = address.getText().toString();

        EditText cardNumber = (EditText) findViewById(R.id.editTextCardNumber);
        String newCardNumber = cardNumber.getText().toString();

        EditText expiration = (EditText) findViewById(R.id.editTextExpirationDate);
        String newExpiration = expiration.getText().toString();

        Client client;

        if (user.getClass() == Admin.class){
            client = new Admin (username, password, lastName, firstName, email
                    , newAddress, newCardNumber, newExpiration);
        } else {
            client = new Client (username, password, lastName, firstName, email
                    , newAddress, newCardNumber, newExpiration);
        }

        //Put new client info into the flightApp then save
        this.flightApp.getUserHashMap().put(this.user.getUsername(), client);
        this.flightApp.savePersistentData();
        this.user = client;

        Toast.makeText(getApplicationContext(),
                "Saved!",
                Toast.LENGTH_SHORT).show();
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
