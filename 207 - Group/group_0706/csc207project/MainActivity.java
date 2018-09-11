package group_0706.csc207project;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import flightapp.*;
import flightapp.MultipleUserException;


public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static final String USER_STORAGE = "users.ser";
    public static final String FLIGHT_STORAGE = "flights.ser";
    public static final String PASSWORD_STORAGE = "password.txt";
    public static final String DATA_DIR = "data_dir";
    public static final String FLIGHT_MANAGER_KEY = "flightManagerKey";
    public static final String USER_MANAGER_KEY = "userManagerKey";
    private FlightApp flightApp;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setContentView(R.layout.activity_main);
        // Find out where this application stores files, and get
        // the directory called USERDATADIR, or if it doesn't exist,
        // create it.
        File dataDir = this.getApplicationContext().getDir(DATA_DIR,
                MODE_PRIVATE);
        String userPath = dataDir.getPath() + "/" + USER_STORAGE;
        String flightPath = dataDir.getPath() + "/" + FLIGHT_STORAGE;
        String passwordPath = dataDir.getPath() + "/" + PASSWORD_STORAGE;

        //Construct the new FlightApp.
        flightApp = new FlightApp(userPath, flightPath, passwordPath);

    }

    public void logIn(View view) {
        EditText usernameField = (EditText)findViewById(R.id.editTextUsername);
        String username = usernameField.getText().toString();
        EditText passwordField = (EditText) findViewById(R.id.password);
        String password = passwordField.getText().toString();
        File dataDir =
                this.getApplicationContext().getDir(DATA_DIR, MODE_PRIVATE);
        //check if the user details are valid.
        boolean userValid = flightApp.isUserValid(username, password);
        if (userValid) {
            User user = flightApp.getUserHashMap().get(username);


            if (user.getClass() == Client.class) {
                //Pass the authenticated user to new Activity...
                intent = new Intent(this, ClientActivity.class);
                intent.putExtra("activeClient", user);
                intent.putExtra("manager", flightApp);
                startActivityForResult(intent, 1);
            } else if (user.getClass() == Admin.class) {
                intent = new Intent(this, AdminActivity.class);
                intent.putExtra("activeAdmin", user);
                intent.putExtra("manager", flightApp);
                startActivityForResult(intent, 2);
            }
        }
        else {
            //Display an eror message...
            Toast.makeText(getApplicationContext(),
                    "Oh no! \n That login failed, try something else",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public void goRegister(View view){

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://group_0706.csc207project/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://group_0706.csc207project/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 || requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                this.flightApp = (FlightApp) data.getSerializableExtra("FlightApp");
            }
        }
    }
}
