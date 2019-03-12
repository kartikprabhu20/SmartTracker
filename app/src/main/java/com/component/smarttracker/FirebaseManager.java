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

    public void updateMessage(ComponentTracker ComponentTracker, String newText) {
        mMessageDatabaseReference.child(ComponentTracker.getComponentKey()).child("text").setValue(newText);
    }

    public void attachDatabaseReadListeners() {

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ComponentTracker com = dataSnapshot.getValue(ComponentTracker.class);
                    com.setComponentKey(dataSnapshot.getKey());
                    Log.i("smart_tracker","addComponent key:"+ com.getComponentKey());
                    mComponentAdapter.addComponent(com);
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
