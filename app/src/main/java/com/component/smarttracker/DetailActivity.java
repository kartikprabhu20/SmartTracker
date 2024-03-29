package com.component.smarttracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.List;

import static com.component.smarttracker.MainActivity.SMART_TRACKER;
import static com.component.smarttracker.MainActivity.USER_EMAIL;
import static com.component.smarttracker.MainActivity.USER_UID;
import static com.component.smarttracker.OptionsActivity.COMPONENT_DETAIL;
import static com.component.smarttracker.OptionsActivity.OPTION_TYPE;

public class DetailActivity extends AppCompatActivity {

    private String componentType ;

    private MaterialButton fab;
    private TextView borrowerName, borrowerTeam, borroweremail,
            lenderName, lenderTeam, lenderemail ;
    private ComponentTracker component;

    private FirebaseManager mFirebaseManager;
    public ComponentAdapter mComponentAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        componentType = getIntent().getStringExtra(OPTION_TYPE);
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(COMPONENT_DETAIL)) {
            component = (ComponentTracker) bundle.getParcelable(COMPONENT_DETAIL);
            Log.i("smart_tracker","Detailactivity:component : "+ component );

            this.setTitle(component.getComponentName());
        }

        List<ComponentTracker> componentList = new ArrayList<>();
        mComponentAdapter = new ComponentAdapter(this, componentList);
        mFirebaseManager =  FirebaseManager.getManager(this, mComponentAdapter);


        initViews();

    }

    private void initViews() {

        fab = findViewById(R.id.fab);
        lenderName = findViewById(R.id.lender_name);
        lenderTeam = findViewById(R.id.lender_team);
        lenderemail = findViewById(R.id.lender_emailid);
        borrowerName = findViewById(R.id.borrower_name);
        borrowerTeam = findViewById(R.id.borrower_team);
        borroweremail = findViewById(R.id.borrower_emailid);

        ((TextView)findViewById(R.id.component_id_value)).setText(component.getComponentKey());
        ((TextView)findViewById(R.id.component_name_value)).setText(component.getComponentName());

        int option = 0;
        if (null != component.getLender() && !component.getLender().isEmpty()
         && null != component.getBorrower() && !component.getBorrower().isEmpty()){
            fab.setVisibility(View.GONE);
            setBorrowerDetails();
            setLenderDetails();

        }else if (null == component.getLender()){
            fab.setText(R.string.Lend);
            fab.setVisibility(View.VISIBLE);
            setBorrowerDetails();
            findViewById(R.id.lender_details_layout).setVisibility(View.GONE);

        }else if (null == component.getBorrower()) {
            fab.setText(R.string.Borrow);
            fab.setVisibility(View.VISIBLE);
            setLenderDetails();
            findViewById(R.id.borrower_details_layout).setVisibility(View.GONE);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptAlert(fab.getText().toString());
            }
        });
    }

    private void promptAlert(final String text) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("lend".equalsIgnoreCase(text) ? "Enter lender details":"Enter borrower details");

        final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogue_box, null);
        alert.setView(layout);

        EditText componentName = layout.findViewById(R.id.alert_component_name);
        componentName.setVisibility(View.GONE);
        LinearLayout ll =  layout.findViewById(R.id.component_detail_ll);
        ll.setVisibility(View.GONE);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText personName = layout.findViewById(R.id.alert_person_name);
                EditText teamName = layout.findViewById(R.id.alert_team_name);
                if ("lend".equalsIgnoreCase(text)) {
                    component.setLender(personName.getText().toString());
                    component.setLenderTeam(teamName.getText().toString());
                    component.setLenderEmail(getApplicationContext().getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_EMAIL, "UNKNOWN"));
                    component.setLenderID(getApplicationContext().getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_UID, "UNKNOWN"));
                }else {
                    component.setBorrower(personName.getText().toString());
                    component.setBorrowerTeam(teamName.getText().toString());
                    component.setBorrowerEmail(getApplicationContext().getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_EMAIL, "UNKNOWN"));
                    component.setBorrowerID(getApplicationContext().getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_UID, "UNKNOWN"));
                }

                mFirebaseManager.updateMessage(component);
                onBackPressed();
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

    private void setLenderDetails() {
        lenderName.setText(component.getLender());
        lenderTeam.setText(component.getLenderTeam());
        lenderemail.setText(component.getLenderEmail());
    }

    private void setBorrowerDetails() {
        borrowerName.setText(component.getBorrower());
        borrowerTeam.setText(component.getBorrowerTeam());
        borroweremail.setText(component.getBorrowerEmail());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(OPTION_TYPE,componentType);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //Only owner should be allowed to delete the component
        if (component.getOwnerID().equalsIgnoreCase(getApplicationContext().getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_UID, "UNKNOWN")) ) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_menu:

                promptDeleteAlert();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void promptDeleteAlert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Are you sure you want to delete the component records?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mFirebaseManager.deleteComponent(component);
                onBackPressed();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alert.create();
        alert.show();

    }

}
