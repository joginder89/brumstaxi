package com.anaadih.brumstaxi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anaadih.brumstaxi.library.UserFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyJourney extends Activity{
	
	public static final String MyPREFERENCES = "BrumsTaxiPrefs" ;
    public String userId;
    SharedPreferences sharedpreferences;
    JSONArray journeyDataJsonArray;
    ListView listview_journeyblock;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_journey);
		
		Initializer();
		sharedpreferences=getSharedPreferences(MyPREFERENCES, 
			      Context.MODE_PRIVATE);
		if(sharedpreferences.contains("userId")){
			new getMyJourney().execute();
		}
		else{
			Intent intent = new Intent(this,Register.class);
	        startActivity(intent);
	        finish();
		}
	}
	
	public void Initializer() {
		listview_journeyblock=(ListView)findViewById(R.id.listview_journeyblock);
	}
	
	class getMyJourney extends AsyncTask<String, String, String> {
		
		ProgressDialog pDialog;
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyJourney.this);
			pDialog.setMessage("Downloading Journey Data ....");
			
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			if(sharedpreferences.contains("userId")) {
				userId = sharedpreferences.getString("userId", "");
				Log.d("is userId","Yes");
				Log.d("userId==>",userId);
				
				UserFunctions userFunction = new UserFunctions();
				json = userFunction.getMyJourneyData(userId);
				Log.d("MyJourneyData",json.toString());
    		}
    		
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			if(pDialog!=null&&!pDialog.isShowing()){
				pDialog.dismiss();
			}
			
			try {
				if(json.getString("success").equalsIgnoreCase("1")) {
					String journeyDataString = json.getString("journeyData");
					journeyDataJsonArray = new JSONArray(journeyDataString);
					int arrayLength = journeyDataJsonArray.length();
					if(arrayLength > 0) {
						Log.e("MyJourney", "Starting");
						CreateMyJourneyBlock journeyBlockObj = new CreateMyJourneyBlock();
						listview_journeyblock.setAdapter(journeyBlockObj);
						listview_journeyblock.setOnItemClickListener(itemClickListener1);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	OnItemClickListener itemClickListener1=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			JSONObject jsonObject;
			String pickUpFromString="";
			String dropOffAtString="";
			String journeyFare="";
			if(journeyDataJsonArray != null){
				try {
					Log.e("MyJopurneyData", "First");
					jsonObject=(JSONObject) journeyDataJsonArray.get(position);
					pickUpFromString=jsonObject.getString("user_request_from_name");
					dropOffAtString=jsonObject.getString("user_request_to_name");
					journeyFare=jsonObject.getString("company_fare_amount");
			        Log.e("MyJopurneyData", "First"+pickUpFromString+dropOffAtString
			        		+journeyFare);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Intent intent=new Intent(MyJourney.this, Booktaxi.class);
			intent.putExtra("pickUpFromString", pickUpFromString);
			intent.putExtra("dropOffAtString", dropOffAtString);
			startActivity(intent);
		}
	};
	private class CreateMyJourneyBlock extends BaseAdapter {

		@Override
		public int getCount() {
			Log.e("CreateMyJourneyBlock Adapter called", "count ");
			return journeyDataJsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			JSONObject jsonObject;
			View row;
            row = inflater.inflate(R.layout.myjourney_block, parent, false);
            TextView pickfrom, dropoff,rate;
            
            pickfrom = (TextView) row.findViewById(R.id.pickfrom);
            dropoff = (TextView) row.findViewById(R.id.dropoff);
            rate = (TextView) row.findViewById(R.id.rate);
            
			try {
				jsonObject = journeyDataJsonArray.getJSONObject(position);
				pickfrom.setText(jsonObject.getString("user_request_from_name"));
				dropoff.setText(jsonObject.getString("user_request_to_name"));
				rate.setText(jsonObject.getString("company_fare_amount"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("MyJourney", "view ");
            return (row);
		}
	}
	
}
