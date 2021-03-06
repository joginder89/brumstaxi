package com.anaadih.brumstaxi;

import org.json.JSONException;
import org.json.JSONObject;

import com.anaadih.brumstaxi.library.UserFunctions;
import com.andreabaccega.widget.FormEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Register extends Activity implements View.OnClickListener {
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	
	Button regButton,callUsButton;
	FormEditText regYourName,regMobile,regUsername,regPassword,regConfirmPassword;
	private ProgressDialog pDialog;
	
	public static final String MyPREFERENCES = "BrumsTaxiPrefs" ;
    public String userId;
    SharedPreferences sharedpreferences;
    String registrationId;
	//Context context = getApplicationContext();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		Initilizer();
		
		regButton.setOnClickListener(this);
		callUsButton.setOnClickListener(this);
	}
	
	private void Initilizer() {
		// TODO Auto-generated method stub
		regButton=(Button)findViewById(R.id.regButton);
		callUsButton=(Button)findViewById(R.id.callUsButton);
		
		regYourName=(FormEditText)findViewById(R.id.regYourName);
		regMobile=(FormEditText)findViewById(R.id.regMobile);
		regUsername=(FormEditText)findViewById(R.id.regUsername);
		regPassword=(FormEditText)findViewById(R.id.regPassword);
		regConfirmPassword=(FormEditText)findViewById(R.id.regConfirmPassword);
	} 

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		String passwordValue=regPassword.getText().toString();
		String confirmPasswordValue=regConfirmPassword.getText().toString();
		
		switch(v.getId()) {
			case R.id.regButton:
				FormEditText[] allFields    = { regYourName,regMobile,regUsername,regPassword,regConfirmPassword };
		        boolean allValid = true;
		        for (FormEditText field: allFields) {
		            allValid = field.testValidity() && allValid;
		        }
		        if(!(passwordValue.equals(confirmPasswordValue))) {
		        	allValid=false;
		        	Toast.makeText(getApplicationContext(),"Confirm Password does not Match",
							   Toast.LENGTH_LONG).show();
		        }
		        if (allValid) {
		        	new AttemptRegister().execute();
		        }
				break;
			case R.id.callUsButton:
				
				break;
		}
	}
	
	class AttemptRegister extends AsyncTask<String, String, String> {
         boolean isError=false; 
         String message="";
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			SharedPreferences prefs = 
					getSharedPreferences(MainActivity.class.getSimpleName(), MODE_PRIVATE);
			registrationId = prefs.getString("registration_id", "");
	        if(registrationId == "") {
	    		try {
		    		
					Log.e("DeviceKey","Device Not Registered");
					Toast.makeText(getApplicationContext(),"Service Not Available",
								   Toast.LENGTH_LONG).show();
					AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
		            builder.setTitle("Device Not Registered!");
		            builder.setMessage("Please check Internet Connection.");
		            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
	
		            public void onClick(DialogInterface dialog, int which) {
		                finish();
		            }
		            });
		            AlertDialog dialog = builder.create();
		            dialog.show();
		            cancel(true);
	    		} catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	        else{
	        	pDialog = new ProgressDialog(Register.this);
				pDialog.setMessage("Attempting Register...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
	       }
			
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			Log.d("I am in ", "doInBackground Function");
			
	        String email = UserInfo.getEmail(getApplicationContext());
	    	
	    	if(email == null) {
	    		email="user not register with Google";
	    	}
			
	    	
	    	
			String name=regYourName.getText().toString();
			String mobile=regMobile.getText().toString();
			String username=regUsername.getText().toString();
			String password=regPassword.getText().toString();
			
			UserFunctions userFunction = new UserFunctions();
			Log.d("isNetworkAvailable=>",String.valueOf(isNetworkAvailable()));
			Log.d("info on Reg. Page",""+name+","+mobile+","+username+","+password+","+registrationId+","+email);
			JSONObject json = userFunction.registerUser(name, mobile,username,password,registrationId,email);
			Log.d("JSON Received on Register Page=>", json.toString());
			// check for login response
			try {
				if(json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						isError=false;
						Log.d("Saving UserId=>", json.getString("userId"));
						  try {
							  sharedpreferences=getSharedPreferences(MyPREFERENCES, 
				    			      Context.MODE_PRIVATE);
							  Editor editor = sharedpreferences.edit();
						      String userId = json.getString("userId");
						      editor.putString("userId", userId);
						      editor.commit();
						      
						      Intent intent = new Intent(Register.this,RegistrationResponse.class);
						         startActivity(intent);
						         finish();
						      
						      if(sharedpreferences.contains("userId")) {
					    			//button.setVisibility(View.VISIBLE);
						    	  Log.d("is userId","Yes");
						    	  Log.d("userId==>",sharedpreferences.getString("userId", ""));
						    	  
									
					    		}
					    		else if(!sharedpreferences.contains("userId")) {
					    			 Log.d("is userId","No");
									
					    		}
					    		else {
					    			Log.d("is userId","There is some Locha");
					    		}
						      
						  } catch (Exception e) {
								e.printStackTrace();
						  }
						
					} else {
						isError=true;
						message=json.getString(KEY_ERROR_MSG);
						Log.d("KEY_ERROR",json.getString(KEY_ERROR));
						
					}
				} else {
					isError=true;
					message="Worng JSON";
						Log.d("JSON","Wrong JSON");
						
				}
			} catch (JSONException e) {
				isError=true;
				message="Please try Again";
				Log.e("JSON","JSONException");
				
				e.printStackTrace();
			}
			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			if(pDialog!=null&&pDialog.isShowing()){
				pDialog.dismiss();
			}
		    if(isError){
		    	Toast.makeText(getApplicationContext(), message, 200).show();
		    	isError=false;
		    }
		}
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}