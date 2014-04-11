package com.anaadih.brumstaxi;

import org.json.JSONException;
import org.json.JSONObject;

import com.anaadih.brumstaxi.Register.AttemptRegister;
import com.anaadih.brumstaxi.library.UserFunctions;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

		TextView myid;
		TextView pickUpFrom;
		TextView dropOffTo;
		TextView bookTaxiDate;
		TextView bookTaxiTime;
		NumberPicker bookTaxiPassengers;
		NumberPicker bookTaxiLuggage;
	  	String user_request_id;
	  	private ProgressDialog pDialog;
	  	private static String KEY_SUCCESS = "success";
		private static String KEY_ERROR = "error";
	    JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quotes);
		myid = (TextView) findViewById(R.id.myid);
		String message = getIntent().getExtras().getString("message");
		String mydata = getIntent().getExtras().getString("mydata");
		
		Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(),mydata,Toast.LENGTH_LONG).show();
		Log.d("ReceivedAtQuoteFile==>",message);
		
		try {
			json = new JSONObject(mydata);
			user_request_id = json.getString("user_request_id");
			String from = json.getString("from");
			String to = json.getString("to");
			String time = json.getString("time");
			String passenger = json.getString("passenger");
			String luggage = json.getString("luggage");
			
			
			pickUpFrom.setText(from);
			dropOffTo.setText(to);
			bookTaxiDate.setText(time);
			bookTaxiTime.setText(time);
			bookTaxiPassengers.setTag(passenger);
			bookTaxiLuggage.setTag(luggage);
			
			//myid.setText(rowid);
			new FetchQuoteData().execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			JSONObject json = userFunction.fetchQuoteData(user_request_id);
			Log.d("JSON Received on Quote Page=>", json.toString());
			// check for login response
			try {
				if(json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						myid.setText(json.toString());
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
}