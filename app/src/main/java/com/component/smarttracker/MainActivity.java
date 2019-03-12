 package com.component.smarttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static com.component.smarttracker.OptionsActivity.BORROWER;
import static com.component.smarttracker.OptionsActivity.FINDER;
import static com.component.smarttracker.OptionsActivity.LENDER;
import static com.component.smarttracker.OptionsActivity.OPTION_TYPE;

 public class MainActivity extends AppCompatActivity implements View.OnClickListener {

     private static final String ANONYMOUS = "anonymous";
     private static final int RC_SIGN_IN = 1;
     private static final String SMART_TRACKER = "smart_tracker";
     private static final String USER_UID = "smart_user_uid";
     public static final String COMPONENT_FINDER = "Component Finder";
     public static final String COMPONENT_LENDER = "Lend Components";
     public static final String COMPONENT_BORROWER = "Borrow Components";

     private Button buttonFind, buttonLend, buttonBorrow;

     private FirebaseAuth mFirebaseAuth;
     private FirebaseAuth.AuthStateListener authStateListener;
     SharedPreferences sharedpreferences;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonFind = findViewById(R.id.button_find);
        buttonLend= findViewById(R.id.button_lend);
        buttonBorrow = findViewById(R.id.button_borrow);
         buttonFind.setOnClickListener(this);
         buttonBorrow.setOnClickListener(this);
         buttonLend.setOnClickListener(this);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



         sharedpreferences = getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE);
         //Auth
         mFirebaseAuth = FirebaseAuth.getInstance();
         authStateListener = new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                 FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


                 if (firebaseUser != null){
                     // logged in

                     SharedPreferences.Editor editor = sharedpreferences.edit();
                     editor.putString(USER_UID, firebaseUser.getUid());
                     editor.commit();

                     onSignedInInit(firebaseUser.getDisplayName());
                 }else{
                     //logged out
                     onSignedOutInit();

                     startActivityForResult(
                             AuthUI.getInstance()
                                     .createSignInIntentBuilder()
                                     .setIsSmartLockEnabled(false)
                                     .setAvailableProviders(Arrays.asList(
                                             new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                             new AuthUI.IdpConfig.EmailBuilder().build()))
                                     .build(),
                             RC_SIGN_IN);

                 }
             }
         };
     }

     private void onSignedOutInit() {
//         mComponentAdapter.clear();
//         mFirebaseManager.detachDatabaseReadListeners();
     }

     private void onSignedInInit(String displayName) {
//         mFirebaseManager.attachDatabaseReadListeners();
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch(item.getItemId()){
             case R.id.sign_out_menu:
                 AuthUI.getInstance().signOut(this);
                 return true;

             default:
                 return super.onOptionsItemSelected(item);
         }
     }

     @Override
     protected void onResume() {
         super.onResume();
         mFirebaseAuth.addAuthStateListener(authStateListener);
     }

     @Override
     protected void onPause() {
         super.onPause();

         if (authStateListener != null)
             mFirebaseAuth.removeAuthStateListener( authStateListener);

//         mFirebaseManager.detachDatabaseReadListeners();

     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == RC_SIGN_IN){
             if (resultCode == RESULT_CANCELED){
                 finish();
             }
         }
     }

     @Override
     public void onClick(View v) {
         Intent intent = new Intent(this, OptionsActivity.class);

         switch (v.getId()){
             case R.id.button_find:
                 intent.putExtra(OPTION_TYPE,FINDER);
                 break;
             case R.id.button_lend:
                 intent.putExtra(OPTION_TYPE,LENDER);
                 break;
             case R.id.button_borrow:
                 intent.putExtra(OPTION_TYPE,BORROWER);
                 break;
         }
         startActivity(intent);
     }
 }
