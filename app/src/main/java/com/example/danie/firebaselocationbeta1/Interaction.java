package com.example.danie.firebaselocationbeta1;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by danie on 26/02/2018.
 */

public class Interaction {
    String phoneNumber;

    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Interaction(String phone_number, Context context_){
        phoneNumber=phone_number;
        context=context_;
    }
    String TAG ="Interaction";
    public void selfData(){

        final DocumentReference docRef2 = db.collection("interactions").document(phoneNumber);
        //final String TAG="docRef";
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // check whether document existed and not empty
                    if (document.exists()&&document != null) {

                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("exist", true);
                        docRef2.set(data1);
                        Log.d(TAG, "Creating personal data");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void connect(final String phoneNumberAdd){

        final DocumentReference docRef2 = db.collection("interactions").document(phoneNumberAdd);
        //final String TAG="docRef";
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // check whether document existed and not empty
                    if (document.exists()&&document != null) {
                        Date now = new Date();
                        String format ="yyyyMMddHHmmss";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
                        Double formattedNow = Double.valueOf(simpleDateFormat.format(now));
                        Log.i("DateTime",String.valueOf(formattedNow));

                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put(phoneNumber, formattedNow);
                        docRef2.update(data1);

                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        CollectionReference interactions = db.collection("interactions");
                        data1.clear();
                        //data1.put("exist", true);
                        data1.put(phoneNumberAdd, formattedNow);

                        interactions.document(phoneNumber).update(data1);

                    } else {

                        Log.d(TAG, "No such person");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void connectPersonalMeeting(final String documentId,final String phoneNumberAdd){

        final DocumentReference docRef2 = db.collection("interactions").document(phoneNumberAdd);
        //final String TAG="docRef";
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // check whether document existed and not empty
                    if (document.exists()&&document != null) {
                        Date now = new Date();
                        String format ="yyyyMMddHHmmss";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
                        Double formattedNow = Double.valueOf(simpleDateFormat.format(now));
                        Log.i("DateTime",String.valueOf(formattedNow));

                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put(documentId, formattedNow);
                        docRef2.update(data1);

                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        CollectionReference interactions = db.collection("interactions");
                        data1.clear();
                        //data1.put("exist", true);
                        data1.put(documentId, formattedNow);

                        interactions.document(phoneNumber).update(data1);

                    } else {

                        Log.d(TAG, "No such person");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void createPersonalMeeting(final String phoneNumberAdd){

        final CollectionReference colRef2 = db.collection("personal meetings");
        //final String TAG="docRef";
        GeoPoint meetingLocation =new GeoPoint(00,00);

        Date now = new Date();

        String format ="yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        final Double formattedNow = Double.valueOf(simpleDateFormat.format(now));
        Log.i("DateTime",String.valueOf(formattedNow));

        // Trying to add user to firestore database
        Map<String, Object> data1 = new HashMap<>();
        data1.put("time", FieldValue.serverTimestamp());
        data1.put("location",meetingLocation);
        data1.put("created by",phoneNumber);
        data1.put("modified by",phoneNumber);
        data1.put(phoneNumber, formattedNow);
        data1.put(phoneNumberAdd,formattedNow);
        data1.put("created at",FieldValue.serverTimestamp());
        colRef2.add(data1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                String documentId=String.valueOf(documentReference.getId());
                /*
                Map<String, Object> data2 = new HashMap<>();
                data2.put(phoneNumber, formattedNow);
                data2.put(phoneNumberAdd,formattedNow);
                colRef2.document(documentReference.getId()).collection("ids").add(data2);
                */

                Log.i(TAG,"Document id "+documentId);
                 connectPersonalMeeting(documentId,phoneNumberAdd);

            }
        });



    }
}
