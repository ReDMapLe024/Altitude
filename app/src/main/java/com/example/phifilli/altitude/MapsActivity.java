package com.example.phifilli.altitude;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.location.LocationListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    public double elevation;
    public static Location loc;
    private ProgressBar spinner;
    private boolean locationFound;

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        locationFound = false;
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                loc = location;
                Log.e("LocationChanged", "LocationChanged");
                locationFound = true;
                handleNewLocation(loc);
                locationManager.removeUpdates(this);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates

        Button calculate = (Button) findViewById(R.id.button_calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                new MyRequest().execute();
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    Log.i("LocationProvider","NETWORK_PROVIDER");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
                }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i("LocationProvider","GPS_PROVIDER");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        TextView tv = (TextView) findViewById(R.id.TV_Altitude);


    }

    @SuppressWarnings({"MissingPermission"})
    /*public void requestLocation(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location == null){
            Log.d(TAG, "Location is null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            Log.d(TAG, "Location found");
            handleNewLocation(location);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected  void onPause(){
        super.onPause();

    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected(@Nullable Bundle bundle) {
        PendingIntent pi = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,pi);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("COORDINATES", location.toString());

    }

    private void handleNewLocation(Location location) {
        while(locationFound != true){

        }
        spinner.setVisibility(View.GONE);
        Log.d(TAG, location.toString());

        DecimalFormat df = new DecimalFormat("#0.00");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        latitude = Double.parseDouble(df.format(latitude));
        longitude = Double.parseDouble(df.format(longitude));
        elevation = Double.parseDouble(df.format(elevation));

        TextView textview_latitude = (TextView) findViewById(R.id.TV_Latitude);
        TextView textview_longitude = (TextView) findViewById(R.id.TV_Longitude);
        TextView textview_altitude = (TextView) findViewById(R.id.TV_Altitude);

        textview_latitude.setText(Double.toString(latitude));
        textview_longitude.setText(Double.toString(longitude));
        textview_altitude.setText(Double.toString(elevation));



        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        Toast toast = Toast.makeText(this, "Location found!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0, 20);
        toast.show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended, Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }




    private class MyRequest extends AsyncTask<Void, Void, Double>{



        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Double doInBackground(Void... params) {
            double el = 0;

            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536,-104.9847034&key=AIzaSyCkP3MDYGCjPPB7Kd-8dGzqnWRIx_bWeA0");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer response = new StringBuffer();

                while((line = in.readLine()) !=null){
                    response.append(line);
                }

                JSONObject jsonObj = new JSONObject(response.toString());
                JSONArray results = jsonObj.getJSONArray("results");
                JSONObject current = results.getJSONObject(0);
                el = Double.parseDouble(current.getString("elevation"));
                Log.d("Elevation", Double.toString(el));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return el;
        }


        protected void onProgressUpdate(Void values) {
            super.onProgressUpdate(values);

        }


        protected void onPostExecute(Double o) {
            Log.d("RESULT", Double.toString(o));
            super.onPostExecute(o);
            elevation = o;



        }
    }
}
