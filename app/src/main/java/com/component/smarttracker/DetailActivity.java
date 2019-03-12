package com.component.smarttracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.component.smarttracker.OptionsActivity.COMPONENT_DETAIL;

public class DetailActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(COMPONENT_DETAIL)) {
            ComponentTracker tracker = (ComponentTracker) bundle.getParcelable(COMPONENT_DETAIL);
            Log.i("smart_tracker","Detailactivity:component : "+ tracker );

            this.setTitle(tracker.getComponentName());
        }

        initViews();

    }

    private void initViews() {

    }
}
