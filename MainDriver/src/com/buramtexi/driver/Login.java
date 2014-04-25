package com.buramtexi.driver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	EditText usernameedit,password;
	TextView loginLabel;
	Button submit;
	SharedPreferences preferences ;
	String name;
	ServerRequest request;
	JSONObject json;
	String id;
	private static Context sContext;
	private ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_driver);
		request = new ServerRequest();
		preferences = getSharedPreferences("driver.data", Context.MODE_PRIVATE);
		usernameedit= (EditText)findViewById(R.id.logindrivername);
		password=(EditText)findViewById(R.id.loginPassword);
		submit =(Button)findViewById(R.id.loginButton);
		loginLabel=(TextView)findViewById(R.id.loginLabel);
		submit.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_driver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_driver,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(usernameedit.getText().toString().equals("") || password.getText().toString().equals(""))
		{					
					Toast toast= Toast.makeText(getApplicationContext(), 
					"Fill All Details", Toast.LENGTH_SHORT);  
					toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
			//Toast.makeText(Login.this, "Fill all fields", Toast.LENGTH_SHORT).show();
		}else{
			//new loadSomeStuff().execute();
			sContext = getApplicationContext();
			new loadSomeStuff().execute();
		}
	}
	
	public class loadSomeStuff extends AsyncTask<String, Integer, String>{

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Attempting Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("type","login"));
	        nameValuePairs.add(new BasicNameValuePair("email",usernameedit.getText().toString()));
	        nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
			
			String data = request.getDataFromServer(ServerRequest.pathLogin,nameValuePairs);
			//loginLabel.setText(data);
			
			return data;
		}
		
		
		protected void onProgressUpdate(Integer... progress) {
			
		}
		protected void onPostExecute(String result){
			pDialog.dismiss();
			Log.d("onPostExecute=>",result);
			if(result!=null&&request.isConnectingToInternet(sContext)){

				
				try {
					json = new JSONObject(result);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Log.d("onPostExecute=>",json.toString());
				//Toast.makeText(MainDriver.this, "Welcome : "+ result, Toast.LENGTH_SHORT).show();
				try {
					if(json.getString("error").equals("-1"))
					{
							    Toast toast= Toast.makeText(getApplicationContext(), 
								"Incorrect Email or Password", Toast.LENGTH_SHORT);  
								toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
								toast.show();
						//Toast.makeText(Login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
					}
					else
					{
						try {
							
							name = json.getString("drivername");
							id =json.getString("driverid");
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString("name",name);
							editor.putString("id", id);
							editor.putString("ll", "login");
							editor.commit(); 
						//Toast.makeText(Login.this, "Login Successful" , Toast.LENGTH_SHORT).show();						
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finish();

						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else{
        		AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Attention!");
                builder.setMessage("Connection to Server failed.");
                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                	 finish();          
                     moveTaskToBack(true);
                }
                });
                AlertDialog dialog = builder.create();
                dialog.show();	
    		}
			
			
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
	     Intent data = new Intent();
		 data.putExtra("returnKey1",id );
     	 setResult(2, data);
		 super.finish();
	}
	@Override
    public void onBackPressed() {
        //super.onBackPressed();   
        //    finish();
		moveTaskToBack(true);
    }
	
}
