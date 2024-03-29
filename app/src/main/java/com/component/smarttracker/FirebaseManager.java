package com.component.smarttracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.component.smarttracker.MainActivity.SMART_TRACKER;
import static com.component.smarttracker.MainActivity.USER_UID;
import static com.component.smarttracker.OptionsActivity.BORROWER;
import static com.component.smarttracker.OptionsActivity.FINDER;
import static com.component.smarttracker.OptionsActivity.LENDER;
import static com.component.smarttracker.OptionsActivity.MYCOMPONENTS;

public class FirebaseManager {

    private static final String TAG = "smart_tracker";
    private final ComponentAdapter mComponentAdapter;
    private final Context mContext;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;


    private ChildEventListener mChildEventListener;

    public FirebaseManager(Context context, ComponentAdapter componentAdapter) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("component");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("component_photo");

        mContext = context;
        mComponentAdapter = componentAdapter;
    }

    public static FirebaseManager getManager(Context context, ComponentAdapter componentAdapter){
        return new FirebaseManager(context,componentAdapter);
    }

    public void sendMessage(ComponentTracker componentTracker) {
        mMessageDatabaseReference.push().setValue(componentTracker);
    }

    public void updateMessage(ComponentTracker componentTracker) {
        mMessageDatabaseReference.child(componentTracker.getComponentKey()).setValue(componentTracker);
    }

    public void deleteComponent(ComponentTracker componentTracker) {
        mMessageDatabaseReference.child(componentTracker.getComponentKey()).removeValue();

    }

    public void attachDatabaseReadListeners(final String componentType) {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ComponentTracker comp = dataSnapshot.getValue(ComponentTracker.class);
                    comp.setComponentKey(dataSnapshot.getKey());
                    Log.i("smart_tracker","addComponent key:"+ comp.getComponentKey());

                    String userID = mContext.getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_UID, "UNKNOWN");
                    if (FINDER.equalsIgnoreCase(componentType) || doesComponentTypeExists(componentType,comp)
                    || (MYCOMPONENTS.equalsIgnoreCase(componentType) && ((null != comp.getLenderID() && comp.getLenderID().equalsIgnoreCase(userID)) || (null != comp.getBorrowerID() && comp.getBorrowerID().equals(userID) ))))
                        mComponentAdapter.addComponent(comp);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ComponentTracker comp = dataSnapshot.getValue(ComponentTracker.class);
                    comp.setComponentKey(dataSnapshot.getKey());
                    Log.i("smart_tracker","updateComponent key:"+ comp.getComponentKey());

                        mComponentAdapter.updateComponent(comp);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    ComponentTracker comp = dataSnapshot.getValue(ComponentTracker.class);
                    comp.setComponentKey(dataSnapshot.getKey());
                    Log.i("smart_tracker","removeComponent key:"+ comp.getComponentKey());
                    mComponentAdapter.removeComponent(comp);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            };

            mMessageDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private boolean doesComponentTypeExists(String componentType, ComponentTracker comp) {
        if ((LENDER.equalsIgnoreCase(componentType) && null == comp.getBorrower() )
                || BORROWER.equalsIgnoreCase(componentType) && null == comp.getLender())
            return true;
        return false;
    }

    public void detachDatabaseReadListeners() {
        if (mChildEventListener != null) {
            mMessageDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    public StorageReference getStorageReference(String segment) {
        return mStorageReference.child(segment);
    }

}
