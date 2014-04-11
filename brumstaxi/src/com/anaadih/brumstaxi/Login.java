package com.anaadih.brumstaxi;

//import com.anaadih.brumstaxi.library.DatabaseHandler;
import com.anaadih.brumstaxi.library.UserFunctions;
import com.andreabaccega.widget.FormEditText;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
public class Login extends Activity implements View.OnClickListener {
	
	Button loginButton,logoButton;
	FormEditText loginUsername,loginPassword;
	private ProgressDialog pDialog;
	boolean allValid = true;
	
		// JSON Response node names
		private static String KEY_SUCCESS = "success";
		private static String KEY_ERROR = "error";
		private static String KEY_ERROR_MSG = "error_msg";
		private static String KEY_UID = "userid";
		private static String KEY_USERNAME = "username";
		//private static String KEY_PASSWORD = "password";
		private static String KEY_CREATED_AT = "createdtime";
		//private static String KEY_UPDATED_AT = "updatedtime";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		Initilizer();
		logoButton.setOnClickListener(this);
		loginButton.setOnClickListener(this);
	}
	
	private void Initilizer() {
		// TODO Auto-generated method stub
		logoButton=(Button)findViewById(R.id.loginLogo);
		loginButton=(Button)findViewById(R.id.loginButton);
		
		loginUsername=(FormEditText)findViewById(R.id.loginUsername);
		loginPassword=(FormEditText)findViewById(R.id.loginPassword);
	} 
	
	@Override
	public void onClick(View v) {
		
        
        switch(v.getId()){
			case R.id.loginLogo:
				gotoHomepage();
				break;
			case R.id.loginButton:
				FormEditText[] allFields    = { loginUsername,loginPassword };
		        for (FormEditText field: allFields) {
		            allValid = field.testValidity() && allValid;
		        }
				if (allValid) {
					new AttemptLogin().execute();
		        }
				break;
        }
	}
	
	private void gotoHomepage() {
		Intent intent = new Intent(Login.this,MainActivity.class);
         startActivity(intent);
         finish();
	}
	class AttemptLogin extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			
			String USERNAME=loginUsername.getText().toString();
			String PASSWORD=loginPassword.getText().toString();
			UserFunctions userFunction = new UserFunctions();
			Log.d("Button", "Login");
			JSONObject json = userFunction.loginUser(USERNAME, PASSWORD);
			
			// check for login response
			try {
				if(json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						// user successfully logged in
						// Store user details in SQLite Database
						//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						//JSONObject json_user = json.getJSONObject("user");
						
						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						/*db.addUser(json.getInt(KEY_UID),
								json_user.getString(KEY_USERNAME),  
								json_user.getString(KEY_CREATED_AT));*/					
						
						// Launch Dashboard Screen
						Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
						
						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);
						
						// Close Login Screen
						finish();
					} else {
						pDialog.dismiss();
						Log.d("KEY_ERROR",json.getString(KEY_ERROR));
						Toast.makeText(getApplicationContext(),json.getString(KEY_ERROR_MSG),
									   Toast.LENGTH_LONG).show();
					}
				} else {
						pDialog.dismiss();
						Log.d("JSON","Wrong JSON");
						Toast.makeText(getApplicationContext(),"Worng JSON",
							   Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
		}
	}
}