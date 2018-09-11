package group_0706.csc207project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;

import flightapp.Admin;
import flightapp.FlightApp;

/**
 * An activity that allows an admin to upload flight or client information from a file.
 */
public class UploadInfo extends AppCompatActivity {

    private FlightApp flightApp;
    private Admin user;

    //Paths to CSV files
    private File DIR;
    private String PATH;

    //For File Permissions
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);

        Intent intent = getIntent();

        //Get serialized Info
        this.flightApp = (FlightApp) intent.getSerializableExtra("FlightApp");
        this.user = (Admin) intent.getSerializableExtra("User");

        //Checks if read permissions is given, if not request for such
        if (this.checkCallingOrSelfPermission(Manifest.permission.
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            this.requestPermissions(new String[]{Manifest.permission.
                    READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        //Sets upload directory in External Storage
        DIR = new File(Environment.getExternalStorageDirectory(), "FlightApp");
        PATH = DIR.getPath() + "/";

        //Creates directory if it doesn't exist
        if (! DIR.exists()){
            DIR.mkdirs();
        }

        //Sets editText box hints to tell path to directory
        EditText filePathC = (EditText) findViewById(R.id.editClientUpload);
        filePathC.setHint(PATH);

        EditText filePathF = (EditText) findViewById(R.id.editFlightUpload);
        filePathF.setHint(PATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_info, menu);
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
     * Uploads information for clients from a file with path at the editText for clients.
     *
     * @param view The button for uploading client information.
     */
    public void uploadClient(View view){
        EditText filePath = (EditText) findViewById(R.id.editClientUpload);
        String cPath = filePath.getText().toString();

        try {
            //Addes users and save persisent data
            this.flightApp.addUser(PATH + cPath);
            this.flightApp.savePersistentData();

            Toast.makeText(getApplicationContext(),
                    "Upload successful!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Oh no! \n We couldn't find that file. Try something else", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Uploads information for flights from a file with path at the editText for flights.
     *
     * @param view The button for uploading flight information.
     */
    public void uploadFlight(View view){
        EditText filePath = (EditText) findViewById(R.id.editFlightUpload);
        String fPath = filePath.getText().toString();

        try {
            //Addes flight and saves persistent data
            this.flightApp.addFlight(PATH + fPath);
            this.flightApp.savePersistentData();

            Toast.makeText(getApplicationContext(),
                    "Upload successful!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e){
            Toast.makeText(getApplicationContext(),
                    "Oh no! \n We couldn't find that file. Try something else", Toast.LENGTH_SHORT).show();
        } catch (ParseException e){

        }
    }

    /**
     * Goes to the previous activity and gives it the flightApp and user.
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
     * Resolves Permission Request, specific for this activity
     *
     * @param requestCode code given by request
     * @param permissions permissions that are requested
     * @param grantResults desired results to grant said permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
