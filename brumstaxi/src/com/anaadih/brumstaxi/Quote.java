package com.anaadih.brumstaxi;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
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
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Quote extends Activity {
		
		int user_request_id,i;
		JSONArray quotesJsonArray;
		TextView qtPickingupValue;
		TextView qtDropOffValue;
		TextView qtDateTimeValue;
		TextView qtPassengerValue;
		TextView qtLuggageValue;
		ListView listview_company;
		//LinearLayout parentLayout;
		
		
	  	private ProgressDialog pDialog;
	    JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("THREADTEST","MainThread=>"+Long.toString(Thread.currentThread().getId()));
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
		listview_company=(ListView) findViewById(R.id.listview_company);
		
		// Parent layout
        //parentLayout = (LinearLayout)findViewById(R.id.companyQuoteBody);
        
        
	}
	
	class FetchQuoteData extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		JSONObject jsonResult;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Quote.this);
			pDialog.setMessage("Fetching Quote Data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			Log.d("THREADTEST","PRE=>"+Long.toString(Thread.currentThread().getId()));
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			Log.d("I am in ", "doInBackground Function");
			Log.d("THREADTEST","BACKGROUND=>"+Long.toString(Thread.currentThread().getId()));
			
			if(!isNetworkAvailable()) {
	    		cancel(true);
	    		pDialog.dismiss();
				Log.e("isNetworkAvailable",String.valueOf(isNetworkAvailable()));
				Toast.makeText(getApplicationContext(),"Internet is not Available",
							   Toast.LENGTH_LONG).show();
	    	}
	    	
			UserFunctions userFunction = new UserFunctions();
			jsonResult = userFunction.fetchQuoteData(String.valueOf(user_request_id));
			
			Log.d("JSON Received on Quote Page=>", jsonResult.toString());
			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			Log.d("THREADTEST","POST=>"+Long.toString(Thread.currentThread().getId()));
			pDialog.dismiss();
			try {  /*  quotesJsonArray.getJSONObject(0).getString("company_id").equalsIgnoreCase("getfalse")  */
				if(jsonResult.getString("success").equalsIgnoreCase("1")) {
					String quotesString = jsonResult.getString("quotes");
					//JSONObject quoteDetails = new JSONObject(jsonResult.getString("company_id"));
					quotesJsonArray = new JSONArray(quotesString);
					
					
					int arrayLength = quotesJsonArray.length();
					if(arrayLength>0){
						Log.e("Anurag Company  Adapter called", "Starting ");
						CompanyList companyList=new CompanyList();
						listview_company.setAdapter(companyList);
						listview_company.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								JSONObject jsonObject;
								String comapny="";
								String price="";
								if(quotesJsonArray!=null){
									try {
										Log.e("Quote", "First");
										jsonObject=(JSONObject) quotesJsonArray.get(arg2);
										comapny=jsonObject.getString("company");
								        price=jsonObject.getString("company_fare_amount");
								        Log.e("Quote", "First"+comapny+price);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								Intent intent=new Intent(Quote.this, ConfirmFare.class);
								intent.putExtra("company", comapny);
								intent.putExtra("price", price);
								startActivity(intent);
							}
						});
					}
	
				}
			} catch(JSONException e) {
				Log.e("JSON in Adding Quote","JSONException");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
	
	private class CompanyList extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			Log.e("Anurag Company  Adapter called", "count ");
			return quotesJsonArray.length();
		}

		@Override
		public JSONArray getItem(int position) {
			// TODO Auto-generated method stub
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			JSONObject jsonObject;
			View row;
            row = inflater.inflate(R.layout.quotes_item, parent, false);
            TextView title, price;
            ImageView i1;
            title = (TextView) row.findViewById(R.id.textView_company);
            price = (TextView) row.findViewById(R.id.text_price);
            i1=(ImageView)row.findViewById(R.id.button_accept);
			try {
				jsonObject = quotesJsonArray.getJSONObject(position);
				  title.setText(jsonObject.getString("company"));
		            price.setText(jsonObject.getString("company_fare_amount"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
			Log.e("Anurag Company  Adapter called", "view ");
            

            return (row);
		}
		
	}
}