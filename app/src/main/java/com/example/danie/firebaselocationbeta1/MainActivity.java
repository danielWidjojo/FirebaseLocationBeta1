package com.example.danie.firebaselocationbeta1;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    int locationPermission;
    LocationListener locationListener;
    GeoPoint currentLocation =new GeoPoint(00,00);
    String phoneNumber="+4410";
    String friendNumber="+447940432510";
    String firstName="Test";
    String lastName="Wong";
    String deviceId="860000";
    String uniqueId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                return;
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //accessing location of device
        locationManager=(LocationManager)this.getSystemService(LOCATION_SERVICE);
        // checking whether permission has been given
        locationPermission= ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location Change",location.toString());
                currentLocation =new GeoPoint(location.getLatitude(),location.getLongitude());

                CollectionReference locations = db.collection("location");
                Map<String, Object> data2 = new HashMap<>();
                data2.put("modified", FieldValue.serverTimestamp());
                data2.put("location", currentLocation);
                data2.put("locationShared",true);
                locations.document(phoneNumber).set(data2);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(locationPermission== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        PhoneAuthentication phoneAuthentication=new PhoneAuthentication(this);
        phoneAuthentication.setUpUser(phoneNumber,deviceId,firstName,lastName);


        uniqueId=phoneAuthentication.getUniqueId();
        //interaction.selfData();
        if(!uniqueId.isEmpty()){
            // testing new interaction class
            Interaction interaction=new Interaction(phoneNumber,uniqueId,this);
            interaction.getUniqueId(friendNumber);
            Log.i("UniqueId",uniqueId);
            //interaction.connect(friendNumber);
        }

        //interaction.createPersonalMeeting(friendNumber);
        /*
        for(int i=0;i<5;i++){
            String test="test";
            test=test+String.valueOf(i);
            Log.i("loop",test);


        }

      */



        // Trying to add user interactions to firestore database


    }
    @Override
    protected void onStart() {

        super.onStart();

        FragmentMap fragmentMap=new FragmentMap();
        Fragment currentFragment=new FragmentMap();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, currentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();



    }
}
