package com.infragps.gpstracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by sergey.derevyanko on 8/24/16.
 */
public class TranslucentActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback, Button.OnClickListener{

    private EditText routeNumberEditText;
    private Button startTrackingButton;
    private RegistrerBusApi registrerBusApi;

    private static final String TAG="LOCATION_ACCESS";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(TranslucentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TranslucentActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        setContentView(R.layout.sdmain);
        setupViews();
        registrerBusApi = new RegistrerBusApi();
    }



    private void setupViews() {
        routeNumberEditText = (EditText) findViewById(R.id.routeNumber);
        startTrackingButton = (Button) findViewById(R.id.btnShowLocation);
        startTrackingButton.setOnClickListener(this);
    }

    private void startTrackingService(){
        Intent startActivityIntent = new Intent(this, GPSTrackingService.class);
        startService(startActivityIntent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            Log.i("LOCATION_ACCESS", "Received response for GPS permission request.");
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "LOCATION_ACCESS permission has now been granted. Showing preview.");
                startService(new Intent(this, GPSTrackingService.class));
                finish();

            } else {
                Log.i(TAG, "LOCATION_ACCESS permission was NOT granted.");

            }
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if(button.getText().toString().equalsIgnoreCase("Start Tracking")){
            String routeNumber = routeNumberEditText.getText().toString();
            Log.i(TranslucentActivity.class.getName(), "routeNumber value is: "+routeNumber);
            if(routeNumber.isEmpty()) return;
             String androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TranslucentActivity.class.getName(), "androidId is: "+androidId);
            Log.i(TranslucentActivity.class.getName(), "androidId as int is: "+androidId.hashCode());
            //registrerBusApi.registerBusPost(new Tracker());


//            startTrackingService();
        }
    }
}
