package com.example.test.register;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.test.R;
import com.example.test.constants.Constants;
import com.example.test.handler.RequestHandler;
import com.example.test.login.LoginActivity;
import com.example.test.profile.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegistrationActivity";
    private static final int REQUEST_LOCATION = 1;

    private EditText et_email,et_username,et_password,et_phonenumber;
    private AppCompatSpinner blood_spinner;
    private AppCompatSpinner category_spinner;
    private Button btnDate,btnLocation,btnRegister;
    private TextView tvPreviousSelectedDate,tvRegLogin;
    private TextView tvLat;
    private TextView tvLon;
    private RadioGroup gender_radio_group;
    private RadioButton gender_radio_button;

    private LocationManager locationManager;
    private String lattitude,longitude;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle("Registration Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        makeObj();

        //for blood group spinner
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.blood_group,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        blood_spinner.setAdapter(arrayAdapter);
        blood_spinner.setOnItemSelectedListener(this);

//        String bloodgroup = blood_spinner.getSelectedItem().toString();
//
//        Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
//        intent.putExtra("bloodgroup",bloodgroup);
//        startActivity(intent);

        //for blood category spinner
        ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(this,R.array.category,R.layout.support_simple_spinner_dropdown_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category_spinner.setAdapter(categoryadapter);
        category_spinner.setOnItemSelectedListener(this);

        //for previous donated date selection
        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");
        tvPreviousSelectedDate.setText(date);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });

        //finding the user current location
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                }
            }
        });

        // get selected radio button from radioGroup
        int selectedId = gender_radio_group.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        gender_radio_button = (RadioButton) findViewById(selectedId);
        //Toast.makeText(RegistrationActivity.this, gender_radio_button.getText().toString()+" clicked", Toast.LENGTH_SHORT).show();
        final String radioGender = gender_radio_button.getText().toString();

        //for registering the user
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                Toast.makeText(RegistrationActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        //for login purposes
        tvRegLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });


    }

    private void registerUser() {

        final String email = et_email.getText().toString().trim();
        if (!isEmailValid(email)){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please insert valid email", Toast.LENGTH_SHORT).show();
            et_email.setText("");
        }
        final String username = et_username.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        //final int phonenumber = Integer.parseInt(et_phonenumber.getText().toString().trim());
        String change = et_phonenumber.getText().toString().trim();
        if (change.equals("")){ // detect an empty string and set it to "0" instead
            change = "0";
        }
        int i = Integer.parseInt(change);
        final int phonenumber = i;
        final String donationdate = tvPreviousSelectedDate.getText().toString().trim();
        final String bloodgroup = blood_spinner.getSelectedItem().toString();

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.putExtra("bloodgroup",bloodgroup);
        startActivity(intent);

        final String category = category_spinner.getSelectedItem().toString();
        final String lat = tvLat.getText().toString().trim();
        final String lon = tvLon.getText().toString().trim();
        final String gender = gender_radio_button.getText().toString();

//        progressDialog.setMessage("Registering User...");
//        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("username",username);
                params.put("password",password);
                params.put("phonenumber", String.valueOf(phonenumber));
                params.put("bloodgroup",bloodgroup);
                params.put("lat",lat);
                params.put("lon",lon);
                params.put("category",category);
                params.put("gender",gender);
                params.put("donationdate",donationdate);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        if (email.isEmpty() || username.isEmpty() || et_phonenumber.getText().equals(0) || password.isEmpty() || bloodgroup.isEmpty() ||
            lat.isEmpty() || lon.isEmpty() || category.isEmpty() || gender.isEmpty()){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Invalid Email or Location Unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();
        }

        et_email.setText("");
        et_username.setText("");
        et_phonenumber.setText("");
        et_password.setText("");
        blood_spinner.setSelection(0);
        blood_spinner.setEnabled(true);
        tvLat.setText("");
        tvLon.setText("");
        category_spinner.setSelection(0);
        category_spinner.setEnabled(true);
        gender_radio_button.clearFocus();
        tvPreviousSelectedDate.setText("");
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();

    }

    public boolean isEmailValid(String email)
    {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    //for getting location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                tvLat.setText(lattitude);
                tvLon.setText(longitude);

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                tvLat.setText(lattitude);
                tvLon.setText(longitude);


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                tvLat.setText(lattitude);
                tvLon.setText(longitude);
            }
            else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }

    }

    //Alert message for turning on for GPS
    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void makeObj() {
        et_email = (EditText)findViewById(R.id.et_email);
        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);
        et_phonenumber = (EditText)findViewById(R.id.et_phonenumber);
        blood_spinner = (AppCompatSpinner)findViewById(R.id.blood_spinnner);
        category_spinner = (AppCompatSpinner)findViewById(R.id.category_spinner);
        btnDate = (Button)findViewById(R.id.btnDate);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnLocation = (Button)findViewById(R.id.btnLocation);
        tvPreviousSelectedDate = (TextView) findViewById(R.id.tvPreviousSelectedDate);
        tvRegLogin = (TextView) findViewById(R.id.tvRegLogin);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);
        gender_radio_group = (RadioGroup) findViewById(R.id.gender_radio_group);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String bloodgroup = parent.getItemAtPosition(position).toString();
        //Toast.makeText(getApplicationContext(), bloodgroup, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();

    }

}
