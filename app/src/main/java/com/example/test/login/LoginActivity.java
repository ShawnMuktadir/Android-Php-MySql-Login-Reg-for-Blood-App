package com.example.test.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.test.home.HomeActivity;
import com.example.test.profile.ProfileActivity;
import com.example.test.register.RegistrationActivity;
import com.example.test.sharedprefmanager.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText et_loginusername,et_loginpassword;
    private Button btn_login;
    private Spinner spinner_logincategory;
    private TextView tv_signup;
    public TextView txt_bloodgroup;

    private ProgressDialog progressDialog;

//    private static final String jsonURL = "http://192.168.0.101/android/includes/getjson.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (SharedPreferenceManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return;
        }

        makeObj();


        //for previous blood category selection
        Intent incomingIntent = getIntent();
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        String bloodgroup = incomingIntent.getStringExtra("bloodgroup");
        intent.putExtra("bloodgroup",bloodgroup);
        startActivity(incomingIntent);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        btn_login.setOnClickListener(this);
        tv_signup.setOnClickListener(this);

        //for blood category spinner
        ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(this,R.array.category,android.R.layout.simple_spinner_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_logincategory.setAdapter(categoryadapter);
        spinner_logincategory.setOnItemSelectedListener(this);


    }

    private void makeObj() {
        et_loginusername = (EditText)findViewById(R.id.et_loginusername);
        et_loginpassword = (EditText)findViewById(R.id.et_loginpassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_signup = (TextView) findViewById(R.id.tv_signup);
        spinner_logincategory = (Spinner) findViewById(R.id.spinner_logincategory);
        txt_bloodgroup = (TextView)findViewById(R.id.txt_bloodgroup);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login){
            userLogin();
        }
        else if (v.getId() == R.id.tv_signup){
            Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
            startActivity(intent);
        }
    }


    private void userLogin(){
        final String username = et_loginusername.getText().toString().trim();
        final String password = et_loginpassword.getText().toString().trim();
        final String bloodcategory = spinner_logincategory.getSelectedItem().toString();

        if (username.isEmpty() && password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please Insert Username and Password", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Checking User...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean("error")){
                                SharedPreferenceManager.getInstance(getApplicationContext()).userLogin(
                                                                                            object.getInt("id"),
                                                                                            object.getString("username"),
                                                                                            object.getString("email"),
                                                                                            object.getString("category"));


                                Toast.makeText(getApplicationContext(), "User Login Successful!!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();

                            }else {

                                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Error: ",error.getMessage());

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("username",username);
                map.put("password",password);
                map.put("category",bloodcategory);

                return map;
            }
        };

            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


          //Toast.makeText(getApplicationContext(), "Please fill all the fields!!!", Toast.LENGTH_SHORT).show();



        et_loginusername.setText("");
        et_loginpassword.setText("");
        spinner_logincategory.setSelection(0);
        spinner_logincategory.setEnabled(true);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String bloodcategoryText = parent.getItemAtPosition(position).toString();
        //Toast.makeText(getApplicationContext(), bloodcategoryText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT).show();
    }
}
