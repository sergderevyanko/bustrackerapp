package com.infragps.gpstracker;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.infragps.gpstracker.client.model.Tracker;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by sergey.derevyanko on 8/24/16.
 */
public class TranslucentActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback, Button.OnClickListener{

    private EditText routeNumberEditText;
    private Button startTrackingButton;
    ApiService apiService;
    boolean mBound = false;
    private final Tracker tracker= new Tracker();


    private static final String TAG="LOCATION_ACCESS";

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ApiService.class);
        Log.i(TranslucentActivity.class.getName(), "mConnection is "+mConnection.toString());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.i(TranslucentActivity.class.getName(), "bindService executed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(TranslucentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                (ActivityCompat.checkSelfPermission(TranslucentActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(TranslucentActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    0);
        }
        setContentView(R.layout.sdmain);
        setupViews();
        String androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        tracker.setTrackerId(androidId.hashCode());
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
            if(apiService == null){
                Log.i(TranslucentActivity.class.getName(), "ApiService is null");
            }
            else {
                this.tracker.setRouteNumber(routeNumber);
                new HttpRequestTask().execute();
                Log.i(TranslucentActivity.class.getName(), "Registered tracker id is: "+ tracker.getId());
            }
//            HttpRequestTask httpRequestTask = new HttpRequestTask();
//            httpRequestTask.executeOnExecutor(executorService,new Tracker(androidId.hashCode(), routeNumber));
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Tracker> {
        @Override
        protected Tracker doInBackground(Void... params) {
            Tracker registredTracker = new Tracker();
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String url = "http://bustracking.com.ua//bus/register";
                ResponseEntity<Tracker> registredTrackerResponse = restTemplate.postForEntity(url, tracker, Tracker.class);
                Log.i(TranslucentActivity.class.getName(), "Registered tracker id is: "+ registredTrackerResponse.getBody().getTrackerId());
                registredTracker = registredTrackerResponse.getBody();
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return registredTracker;
        }
        protected void onPostExecute(Tracker result) {
            tracker.setId(result.getId());
            Log.i(TranslucentActivity.class.getName(), "Registered tracker id is: "+ result.getTrackerId());
            Log.i(TranslucentActivity.class.getName(), "Registered id is: "+ result.getId());
        }
`
    }

//    private class HttpRequestTask extends AsyncTask<Tracker, Integer, Tracker> {
//        protected Tracker doInBackground(Tracker... trackers) {
//            Tracker tracker = trackers[0];
//            try {
//                tracker = registrerBusApi.registerBusPost(tracker);
//            }
//            catch (Exception e){
//                Log.e(TranslucentActivity.class.getName(), "Exception occurs in registerBusPost", e);
//                tracker = new Tracker();
//            }
//            return tracker;
//        }
//        protected void onPostExecute(Tracker result) {
//            Log.i(TranslucentActivity.class.getName(), "Registered tracker id is: "+ result.getId());        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }




    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ApiService.LocalBinder binder = (ApiService.LocalBinder) service;
            Log.i(ServiceConnection.class.getName(), "ApiService has been binded");
            apiService = binder.getService();
            mBound = true;
            Log.i(ServiceConnection.class.getName(), "ApiService has been binded 2");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
