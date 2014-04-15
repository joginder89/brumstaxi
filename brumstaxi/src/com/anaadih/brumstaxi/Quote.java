package com.anaadih.brumstaxi;

import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import com.anaadih.brumstaxi.library.UserFunctions;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Quote extends Activity {
		
		int user_request_id,i;
		TextView qtPickingupValue;
		TextView qtDropOffValue;
		TextView qtDateTimeValue;
		TextView qtPassengerValue;
		TextView qtLuggageValue;
		LinearLayout parentLayout;
		
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
		
		//Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(),mydata,Toast.LENGTH_LONG).show();
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
		
		// Parent layout
        parentLayout = (LinearLayout)findViewById(R.id.companyQuoteBody);
        
        
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
					Log.d("KEY_SUCCESS=>", json.getString(KEY_SUCCESS));
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						Log.d("KEY_SUCCESS 2 =>", res);
						/*	
						LinearLayout childLinearLayout = new LinearLayout(getApplicationContext());
				        childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				        childLinearLayout.setPadding(0, 5, 0, 5);
				        childLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				        childLinearLayout.setBackgroundResource(R.color.mainColor);
				        childLinearLayout.setBaselineAligned(false);
				        childLinearLayout.setWeightSum(100);
				        parentLayout.addView(childLinearLayout);
				        
				        TextView companyName= new TextView(getApplicationContext());
				        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
				                0,LayoutParams.MATCH_PARENT, 50);
				        companyName.setLayoutParams(param1);
				        companyName.setTextSize(20);
				        companyName.setPadding(5, 5, 0, 0);
				        companyName.setTextColor(getResources().getColor(R.color.whiteColor));
				        companyName.setText("My company Name");
				        childLinearLayout.addView(companyName);
				        
				        TextView companyFare= new TextView(getApplicationContext());
				        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
				                0,LayoutParams.MATCH_PARENT, 20);
				        companyFare.setLayoutParams(param2);
				        companyFare.setPadding(0, 5, 0, 0);
				        companyFare.setTextColor(getResources().getColor(R.color.whiteColor));
				        companyFare.setTextSize(20);
				        companyFare.setText("11.40");
				        childLinearLayout.addView(companyFare);
				        
				        Button acceptBtn = new Button(getApplicationContext());
				        acceptBtn.setLayoutParams(new LayoutParams(0, 50));
				        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(
				                0,LayoutParams.MATCH_PARENT, 30);
				        acceptBtn.setLayoutParams(param3);
				        companyFare.setPadding(0, 0, 0, 5);
				        acceptBtn.setBackgroundResource(R.drawable.a15);
				        childLinearLayout.addView(acceptBtn);
						*/
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
			} catch (Exception e) {
				e.printStackTrace();
				pDialog.dismiss();
				Log.e("Exception=>","There is Exception");
				Toast.makeText(getApplicationContext(),"Please try Again",
					   Toast.LENGTH_LONG).show();
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
	
	@SuppressLint("SimpleDateFormat")
	private String getDate(String timeStampStr){

		long dv = Long.valueOf(timeStampStr)*1000;// its need to be in milisecond
		java.util.Date df = new java.util.Date(dv);
		String vv = new SimpleDateFormat("MMMM dd, hh:mm a").format(df);
		return vv;
	}
}