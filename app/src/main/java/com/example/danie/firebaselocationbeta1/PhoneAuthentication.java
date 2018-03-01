package com.example.danie.firebaselocationbeta1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by danie on 22/02/2018.
 */

public class PhoneAuthentication extends AppCompatActivity {
    //String phoneNumber;
    FirebaseAuth mAuth;
    String  mVerificationId;
    Context context;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressDialog progress ;
    SharedPreferences sharedPreferences;
    //to create index able field
    Date now = new Date();
    String format ="yyyyMMddHHmmss";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    Double formattedNow = Double.valueOf(simpleDateFormat.format(now));

    public PhoneAuthentication(Context context_){
        //phoneNumber=phone_number;
        context=context_;
        //context is only needed to display progress dialog
        progress = new ProgressDialog(context);
        sharedPreferences=context_.getSharedPreferences("com.example.danie.firestoretesting",Context.MODE_PRIVATE);
    }
    // Firebase authentication testing
    final String TAG ="Verification";

    public Boolean checkAuthentication(){
        mAuth= FirebaseAuth.getInstance();

        if(null == mAuth.getCurrentUser()) {
            Log.i("Verification","User is not signed in");

            return false;
        }
        else {
            // getting user detail from the authentication system
            // no longer used as user data is now stored in Firestore instead
            /*
            Log.i("Verification","User is signed in");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                //String email = user.getEmail();
                //Uri photoUrl = user.getPhotoUrl();

                // Check if user's email is verified
                //boolean emailVerified = user.isEmailVerified();

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.

                Log.i("User info",name);
            }
            */

            return true;
        }
    }
    //ProgressDialog progress = new ProgressDialog(context);
    public void authenticate(final String phoneNumber){
        mAuth= FirebaseAuth.getInstance();


        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        if(null == mAuth.getCurrentUser()) {

            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:" + credential);

                    signInWithPhoneAuthCredential(credential);
                    storeAuthenticatedNumber(phoneNumber);
                    progress.dismiss();


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                    }
                    progress.dismiss();

                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;

                    // ...
                }
                @Override
                public void onCodeAutoRetrievalTimeOut(String verificationId){
                    progress.dismiss();

            }
            };
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks

        }else {



        }}

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // sign in the user through FireBaseAuth
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String TAG="Sign In";
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }

                        }
                    }
                });

    }
    public void signOut(){
        // to sign out from the authentication system
        mAuth= FirebaseAuth.getInstance();
        mAuth.signOut();
        deleteStoredAuthenticatedNumber();
    }
    public void deleteStoredAuthenticatedNumber(){
        // deleting the phone number that is authenticated to a shared preferences
        sharedPreferences.edit().remove("authenticatedNumber").apply();
    }
    public void storeAuthenticatedNumber(String phoneNumber){
        // storing the phone number that is authenticated to a shared preferences
        sharedPreferences.edit().putString("authenticatedNumber",phoneNumber).apply();
        Log.i(TAG,"Authenticated Number has been stored");
    }
    public String getAuthenticatedNumber(){
        // get the phone number that is authenticated to a shared preferences
        String authenticatedNumber=sharedPreferences.getString("authenticatedNumber","");
        return authenticatedNumber;
    }
    public void setUpUser(final String phoneNumber,final String deviceId,final String firstName,final String lastName){
        //reading data from firebase
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("users");
        // Checking whether user has existed by quering
        userRef.whereEqualTo("phoneNumber",phoneNumber).whereEqualTo("exist",true)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // Checking whether task is empty
                    // If not empty just update user data *for now
                    //If empty than user did not exist
                    if(!task.getResult().isEmpty()){
                    for (DocumentSnapshot document : task.getResult()) {
                        // Need to prevent the system for having the same phone number with more than one document id
                        //Logging id of document
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        CollectionReference users = db.collection("users");
                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("firstName", firstName);
                        data1.put("lastName", lastName);
                        data1.put("modifiedAt", FieldValue.serverTimestamp());
                        data1.put("locationShared",true);
                        users.document(document.getId()).update(data1);

                    }}else {
                        Log.d(TAG, "No such document");
                        CollectionReference users = db.collection("users");
                        // Trying to add user to firestore database
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("firstName", firstName);
                        data1.put("lastName", lastName);
                        data1.put("phoneNumber",phoneNumber);
                        // exist is created in case user decided to delete the account
                        // value will then be set to false
                        data1.put("exist",true);
                        data1.put("modifiedAt", FieldValue.serverTimestamp());
                        data1.put("createdAt", FieldValue.serverTimestamp());
                        // device id is created to verify that the same phone is still utilized
                        // in case somebody use some other phone with the same number
                        // if this happens the device id will be different
                        // the account will then be deleted to prevent data leaks
                        data1.put("deviceId", deviceId);
                        data1.put("locationShared",true);
                        // Set is not used instead add is used
                        // Adding data to get a unique User id as document
                        users.add(data1);
                    }
                }
            }
        });

    }
}





