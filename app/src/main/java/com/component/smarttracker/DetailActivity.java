package com.component.smarttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.component.smarttracker.OptionsActivity.COMPONENT_DETAIL;
import static com.component.smarttracker.OptionsActivity.OPTION_TYPE;

public class DetailActivity extends AppCompatActivity {

    private String componentType ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        componentType = getIntent().getStringExtra(OPTION_TYPE);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(OPTION_TYPE,componentType);

        setResult(RESULT_OK, intent);
        finish();
    }
}
