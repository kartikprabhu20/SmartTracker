package com.component.smarttracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.component.smarttracker.MainActivity.COMPONENT_BORROWER;
import static com.component.smarttracker.MainActivity.COMPONENT_FINDER;
import static com.component.smarttracker.MainActivity.COMPONENT_LENDER;

public class OptionsActivity extends AppCompatActivity {

    public static final String OPTION_TYPE = "option_type";
    public static final String COMPONENT_DETAIL = "component_detail";
    private static final int RC_PHOTO_PICKER = 5;

    //Option types
    public static final String LENDER = "lender";
    public static final String BORROWER = "borrower";
    public static final String FINDER = "finder";
    private static final int REQUEST_CODE = 101;

    private FirebaseManager mFirebaseManager;
    public ComponentAdapter mComponentAdapter;
    private String mUsername;
    private String componentType;

    private RecyclerView mRecyclerView;
    private TextView mNoComponentMessage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("smart_tracker","OptionActivity:onCreate : ");

        super.onCreate(savedInstanceState);
        this.setTitle(getTitle(getIntent().getStringExtra(OPTION_TYPE)));
        setContentView(R.layout.activity_component_list);
        componentType = getIntent().getStringExtra(OPTION_TYPE);

        // Initialize references to views
        mRecyclerView = findViewById(R.id.componentListRecyclerView);
        mNoComponentMessage = findViewById(R.id.no_component_message);

        List<ComponentTracker> componentList = new ArrayList<>();
        mComponentAdapter = new ComponentAdapter(this, componentList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mComponentAdapter);

        //Initialize firebase
        mFirebaseManager =  FirebaseManager.getManager(this, mComponentAdapter);
        mFirebaseManager.attachDatabaseReadListeners(componentType);

        ItemClicklistener itemClicklistener = new ItemClicklistener() {
            @Override
            public void onItemClick(View v, final int position, boolean isLongClick) {

                ComponentTracker componentTracker = mComponentAdapter.getComponentList().get(position);
                if (!isLongClick) {
                    Intent intent = new Intent();
                    intent.setClass(OptionsActivity.this, DetailActivity.class);
                    intent.putExtra(COMPONENT_DETAIL,componentTracker);
                    intent.putExtra(OPTION_TYPE, componentType);
                    startActivityForResult(intent, REQUEST_CODE);
                }else {

                    Toast.makeText(getApplication(), "#" + position + " - " + componentTracker + " (long click)", Toast.LENGTH_SHORT).show();

                }
            }
        };
        mComponentAdapter.attachListener(itemClicklistener);

        if (mComponentAdapter.getComponentList().isEmpty()){
            mNoComponentMessage.setVisibility(View.VISIBLE);
        }else{
            mNoComponentMessage.setVisibility(View.GONE);
        }

        FloatingActionButton fab = findViewById(R.id.fab_add_component);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                promptAlert();
            }
        });

        Log.i("Smart_tracker", "list:"+ mComponentAdapter.getComponentList().size() );

    }

    private void promptAlert() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(componentType == LENDER? "Enter lender details":"Enter borrower details");

        final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogue_box, null);
        alert.setView(layout);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                EditText componentName = layout.findViewById(R.id.alert_component_name);
                EditText personName = layout.findViewById(R.id.alert_person_name);
                EditText teamName = layout.findViewById(R.id.alert_team_name);
                ImageButton imageButton = findViewById(R.id.photo_picker);

//                imageButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        launchPhotoPicker();
//                    }
//                });

                ComponentTracker tracker = new ComponentTracker(personName.getText().toString(),teamName.getText().toString(), componentType);
                tracker.setComponentName(componentName.getText().toString());

                mFirebaseManager.sendMessage(tracker);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alert.create();
        alert.show();
    }

    private void launchPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseManager.attachDatabaseReadListeners(componentType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseManager.detachDatabaseReadListeners();
        mComponentAdapter.clear();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            componentType = data.getStringExtra(OPTION_TYPE);
        }
    }

    private String getTitle(String optionType) {
        switch (optionType){
            case FINDER :
                return COMPONENT_FINDER;
            case  BORROWER :
                return COMPONENT_BORROWER;
            case LENDER :
                return COMPONENT_LENDER;

                default:
                    return FINDER;
        }
    }


}
