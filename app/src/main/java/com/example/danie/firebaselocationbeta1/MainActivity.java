package com.example.danie.firebaselocationbeta1;

import android.*;
import android.Manifest;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LocationManager locationManager;
    int locationPermission;
    LocationListener locationListener;
    GeoPoint currentLocation =new GeoPoint(00,00);
    String phoneNumber="+4410";
    String friendNumber="+447940432510";
    String firstName="Test";
    String lastName="Wong";
    String deviceId="860000";
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
        //reading data from firebase
        DocumentReference docRef = db.collection("users").document(phoneNumber);
        final String TAG="docRef";
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // check whether document existed and not empty
                    if (document.exists()&&document != null) {
                        CollectionReference users = db.collection("users");
                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("firstName", firstName);
                        data1.put("lastName", lastName);
                        //data1.put("modified", FieldValue.serverTimestamp());
                        data1.put("createdAt", FieldValue.serverTimestamp());

                        data1.put("locationShared",true);
                        users.document(phoneNumber).update(data1);

                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        CollectionReference users = db.collection("users");
                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("firstName", firstName);
                        data1.put("lastName", lastName);
                        data1.put("modifiedAt", FieldValue.serverTimestamp());
                        data1.put("createdAt", FieldValue.serverTimestamp());
                        data1.put("deviceId", deviceId);
                        data1.put("locationShared",true);
                        users.document(phoneNumber).set(data1);

                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


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

        // testing new interaction class
        Interaction interaction=new Interaction(phoneNumber,this);
        interaction.selfData();
        interaction.connect(friendNumber);
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
}
