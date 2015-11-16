/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    private RadioGroup radioBoundaryGroup;
    private RadioButton radioBoundaryButton;
    private Button btnDisplay;

    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        RadioGroup radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioBoundaryGroup.setVisibility(View.INVISIBLE);
        Button button = (Button) findViewById(R.id.btnDisplay);
        button.setVisibility(View.INVISIBLE);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setEmptyView(findViewById(R.id.empty));

      //  addListenerOnButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal ) {


         /*   btnDisplay = (Button) findViewById(R.id.btnDisplay);

            btnDisplay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // get selected radio button from radioGroup
                    int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioBoundaryButton = (RadioButton) findViewById(selectedId);

                }


            }*/
        startActivity(new Intent(this, LegalInfoActivity.class));
        return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        Log.d("aalo111","yoo111");

        btnDisplay.setVisibility(View.VISIBLE);
        radioBoundaryGroup.setVisibility(View.VISIBLE);

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();
                Log.d("aalo","yo00o");

//                startActivity(new Intent(MainActivity.this, LegalInfoActivity.class));
                Log.d("aalo","yo1o");
                Intent i = new Intent(MainActivity.this, demo.activityClass);
                String strName = null;
                i.putExtra("Boundary", radioBoundaryButton.getText());
                //            DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);
//                startActivity(new Intent(MainActivity.this, TopView.class));
                startActivity(i);
                //startActivity(new Intent(MainActivity.this, MarkerDemoActivity.class));

                Log.d("aalo","yo2o");

                //return true;

            }

        });



    }

    public void addListenerOnButton() {

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        Log.d("aalo111","yoo111");

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();

            }

        });

    }


}
