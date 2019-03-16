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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class OptionsActivity extends AppCompatActivity {

    public static final String OPTION_TYPE = "option_type";
    public static final String COMPONENT_DETAIL = "component_detail";

    public static final String COMPONENT_LENDER = "Lend Components";
    public static final String COMPONENT_FINDER = "Component Finder";
    public static final String COMPONENT_BORROWER = "Borrow Components";
    public static final String MY_COMPONENTS = "My Components";

    //Option types
    public static final String LENDER = "lender";
    public static final String BORROWER = "borrower";
    public static final String FINDER = "finder";
    public static final String MYCOMPONENTS = "myComponents";

    private static final int REQUEST_CODE = 233;
    private static final int RC_PHOTO_PICKER = 5;


    private FirebaseManager mFirebaseManager;
    public ComponentAdapter mComponentAdapter;
    private String componentType;

    private RecyclerView mRecyclerView;
    private TextView mNoComponentMessage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        componentType = null != getIntent().getStringExtra(OPTION_TYPE) ? getIntent().getStringExtra(OPTION_TYPE) : FINDER;
        this.setTitle(getTitle(componentType));
        setContentView(R.layout.activity_component_list);

        // Initialize references to views
        mRecyclerView = findViewById(R.id.componentListRecyclerView);
        mNoComponentMessage = findViewById(R.id.no_component_message);

        List<ComponentTracker> componentList = new ArrayList<>();
        mComponentAdapter = new ComponentAdapter(this, componentList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mComponentAdapter);

        //Initialize firebase
        mFirebaseManager =  FirebaseManager.getManager(this, mComponentAdapter);
        mFirebaseManager.attachDatabaseReadListeners(componentType);

        ItemClicklistener itemClicklistener = new ItemClicklistener() {
            @Override
            public void onItemClick(View v, final int position, boolean isLongClick) {

                ComponentTracker componentTracker = mComponentAdapter.getComponentList().get(position);
                if (!isLongClick) {
                    Intent intent = new Intent(OptionsActivity.this, DetailActivity.class);
                    intent.putExtra(COMPONENT_DETAIL,componentTracker);
                    intent.putExtra(OPTION_TYPE, componentType);
                    startActivityForResult(intent, REQUEST_CODE);
                }else {

                    Toast.makeText(getApplication(), "#" + position + " - " + componentTracker + " (long click)", Toast.LENGTH_SHORT).show();

                }
            }
        };
        mComponentAdapter.attachListener(itemClicklistener);
        mComponentAdapter.attachComponentListListeener(new ComponentAdapter.ComponentListListener() {
            @Override
            public void checkComponentList() {
                if (mComponentAdapter.getComponentList().isEmpty()){
                    mNoComponentMessage.setVisibility(View.VISIBLE);
                }else{
                    mNoComponentMessage.setVisibility(View.GONE);
                }
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab_add_component);

        if (componentType.equalsIgnoreCase(LENDER) || componentType.equalsIgnoreCase(BORROWER)) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    promptAlert();
                }
            });
        }else {
            fab.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FINDER.equalsIgnoreCase(componentType)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();

            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mComponentAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            return true;
        }
        return false;
    }

    private void promptAlert() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(componentType.equalsIgnoreCase(LENDER)? "Enter lender details":"Enter borrower details");

        final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogue_box, null);
        alert.setView(layout);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                EditText componentName = layout.findViewById(R.id.alert_component_name);
                EditText personName = layout.findViewById(R.id.alert_person_name);
                EditText teamName = layout.findViewById(R.id.alert_team_name);

                ComponentTracker tracker = new ComponentTracker(getApplicationContext(),personName.getText().toString(),teamName.getText().toString(), componentType);
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

        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                componentType = data.getStringExtra(OPTION_TYPE);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        mFirebaseManager.attachDatabaseReadListeners(componentType);
    }

    private String getTitle(String optionType) {
        switch (optionType){
            case FINDER :
                return COMPONENT_FINDER;
            case  BORROWER :
                return COMPONENT_BORROWER;
            case LENDER :
                return COMPONENT_LENDER;
            case MYCOMPONENTS :
                return MY_COMPONENTS;
                default:
                    return FINDER;
        }
    }


}
