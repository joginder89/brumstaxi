package com.anaadih.brumstaxi;

import java.sql.Date;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import com.anaadih.brumstaxi.Register.AttemptRegister;
import com.anaadih.brumstaxi.library.UserFunctions;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class Quote extends Activity {
		
		int user_request_id;
		TextView qtPickingupValue;
		TextView qtDropOffValue;
		TextView qtDateTimeValue;
		TextView qtPassengerValue;
		TextView qtLuggageValue;
		
	  	private ProgressDialog pDialog;
	  	private static String KEY_SUCCESS = "success";
		private static String KEY_ERROR = "error";
	    JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quotes);
		Initializer();
		
		String message = getIntent().getExtras().getString("message");
		String mydata = getIntent().getExtras().getString("mydata");
		
		Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(),mydata,Toast.LENGTH_LONG).show();
		Log.d("ReceivedAtQuoteFile==>",message);
		
		try {
			json = new JSONObject(mydata);
			user_request_id = json.getInt("user_request_id");
			
			String from = json.getString("from");
			String to = json.getString("to");
			String time = json.getString("time");
			String passenger = json.getString("passenger");
			String luggage = json.getString("luggage");
			
			qtPickingupValue.setText(from);
			qtDropOffValue.setText(to);
			qtDateTimeValue.setText(getDate(time));
			qtPassengerValue.setText(passenger);
			qtLuggageValue.setText(luggage);
			
			new FetchQuoteData().execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void Initializer() {
		
		qtPickingupValue=(TextView)findViewById(R.id.qtPickingupValue);
		qtDropOffValue=(TextView)findViewById(R.id.qtDropOffValue);
		qtDateTimeValue=(TextView)findViewById(R.id.qtDateTimeValue);
		qtPassengerValue=(TextView)findViewById(R.id.qtPassengerValue);
		qtLuggageValue=(TextView)findViewById(R.id.qtLuggageValue);
	}
	
	class FetchQuoteData extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Quote.this);
			pDialog.setMessage("Fetching Quote Data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			Log.d("I am in ", "doInBackground Function");
			
			if(!isNetworkAvailable()) {
	    		cancel(true);
	    		pDialog.dismiss();
				Log.e("isNetworkAvailable",String.valueOf(isNetworkAvailable()));
				Toast.makeText(getApplicationContext(),"Internet is not Available",
							   Toast.LENGTH_LONG).show();
	    	}
	    	
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.fetchQuoteData(String.valueOf(user_request_id));
			Log.d("JSON Received on Quote Page=>", json.toString());
			// check for login response
			try {
				if(json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						
					} else {
						pDialog.dismiss();
						Log.d("KEY_ERROR",json.getString(KEY_ERROR));
						Toast.makeText(getApplicationContext(),"Not able to fetch Data",
									   Toast.LENGTH_LONG).show();
					}
				} else {
						pDialog.dismiss();
						Log.d("JSON","Wrong JSON");
						Toast.makeText(getApplicationContext(),"Worng Data",
							   Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				pDialog.dismiss();
				Log.e("JSON","JSONException");
				Toast.makeText(getApplicationContext(),"Please try Again",
					   Toast.LENGTH_LONG).show();
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
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private String getDate(String timeStampStr){

		long dv = Long.valueOf(timeStampStr)*1000;// its need to be in milisecond
		java.util.Date df = new java.util.Date(dv);
		String vv = new SimpleDateFormat("MMMM dd, hh:mm a").format(df);
		return vv;
	}
}