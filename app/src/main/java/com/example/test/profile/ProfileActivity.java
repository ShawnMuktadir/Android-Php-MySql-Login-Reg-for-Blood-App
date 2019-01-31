package com.example.test.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.login.LoginActivity;
import com.example.test.sharedprefmanager.SharedPreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView txt_username,txt_bloodgroup,txt_catagory;
    private static final String jsonUrl = "http://192.168.0.101/Blood/includes/getjson.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        makeObj();

        getSupportActionBar().setTitle("Profile Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        try {
//            JSONArray arr = new JSONArray(jsonUrl);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONObject jObj = null;
//        try {
//            JSONArray arr = new JSONArray(jsonUrl);
//            jObj = arr.getJSONObject(0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (jObj != null) {
//                String date = jObj.getString("bloodgroup");
//                //txt_bloodgroup.setText(date);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        String json=jsonUrl;
//        JSONObject jsonOb = null;
//        try {
//            jsonOb = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONArray arrJson= null;
//        try {
//            arrJson = Objects.requireNonNull(jsonOb).getJSONArray("bloodgroup");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        for(int i = 0; i<Objects.requireNonNull(arrJson).length(); i++) {
//            try {
//                txt_bloodgroup.setText((arrJson.getString(i)));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        //getting the intent
//        Bundle extras = getIntent().getExtras();
//        if (extras != null)
//        {
//            String myParam=extras.getString("paramName");
//            txt_bloodgroup.setText(myParam);
//        }
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("paramName");
                txt_bloodgroup.setText(newString);
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("paramName");
            txt_bloodgroup.setText(newString);
        }

        //check the login activity
        if (!SharedPreferenceManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        txt_username.setText(SharedPreferenceManager.getInstance(this).getUsername());
        txt_catagory.setText(SharedPreferenceManager.getInstance(this).getUsercategory());

        //for previous blood category selection
        Intent incomingIntent = getIntent();
        String bloodgroup = incomingIntent.getStringExtra("bloodgroup");
        txt_bloodgroup.setText(bloodgroup);

    }

    private void makeObj() {
        txt_username = (TextView)findViewById(R.id.txt_username);
        txt_bloodgroup = (TextView)findViewById(R.id.txt_bloodgroup);
        txt_catagory = (TextView)findViewById(R.id.txt_catagory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                SharedPreferenceManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

        }
        return true;
    }
}
