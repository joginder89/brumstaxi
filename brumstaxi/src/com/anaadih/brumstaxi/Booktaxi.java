package com.anaadih.brumstaxi;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.anaadih.brumstaxi.library.UserFunctions;
import com.andreabaccega.widget.FormEditText;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Booktaxi extends Activity implements View.OnClickListener,OnItemClickListener {
	
	FormEditText bookTaxiComment;
	AutoCompleteTextView pickUpFrom,dropOffTo;
	Button getQuoteButton,callUsButton,buttonSelectDate,buttonSelectTime;
	TextView bookTaxiDate,bookTaxiTime;
	NumberPicker bookTaxiPassengers,bookTaxiLuggage;
	private ProgressDialog pDialog;
	boolean allValid = true;
	public static final String MyPREFERENCES = "BrumsTaxiPrefs" ;
    public String userId;
    SharedPreferences sharedpreferences;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID=1;
	public  int year,month,day,hour,minute,second=00;  
	public  int yearSelected,monthSelected,daySelected,hourSelected,minuteSelected;
	private int mYear, mMonth, mDay,mHour,mMinute; 
	
	// JSON Response node names
			private static String KEY_SUCCESS = "success";
			private static String KEY_ERROR_MSG = "error_msg";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        String refUserId=sharedpreferences.getString("userId", "");
		
        if(refUserId == "") {
        	Intent intent = new Intent(this,Register.class);
	        startActivity(intent);
	        finish();	
		}
        
		setContentView(R.layout.book_a_texi);
		
		Initilizer();
		buttonSelectDate.setOnClickListener(this);
		buttonSelectTime.setOnClickListener(this);
		getQuoteButton.setOnClickListener(this);
		callUsButton.setOnClickListener(this);
		
		bookTaxiPassengers.setMaxValue(9);
		bookTaxiPassengers.setMinValue(1);
		
		bookTaxiLuggage.setMaxValue(9);
		bookTaxiLuggage.setMinValue(0);
		
		pickUpFrom.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        pickUpFrom.setOnItemClickListener(this);
        
        dropOffTo.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        dropOffTo.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
	
	
	private void Initilizer() {
		buttonSelectDate=(Button)findViewById(R.id.buttonSelectDate);
		buttonSelectTime=(Button)findViewById(R.id.buttonSelectTime);
		getQuoteButton=(Button)findViewById(R.id.getQuoteButton);
		callUsButton=(Button)findViewById(R.id.callUsButton);
		
		bookTaxiDate=(TextView)findViewById(R.id.bookTaxiDate);
		bookTaxiTime=(TextView)findViewById(R.id.bookTaxiTime);
		
		bookTaxiPassengers = (NumberPicker) findViewById(R.id.bookTaxiPassengers);
		bookTaxiLuggage = (NumberPicker) findViewById(R.id.bookTaxiLuggage);
		
		pickUpFrom = (AutoCompleteTextView) findViewById(R.id.pickUpFrom);
		dropOffTo = (AutoCompleteTextView) findViewById(R.id.dropOffTo);
		bookTaxiComment = (FormEditText) findViewById(R.id.bookTaxiComment);
		
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.buttonSelectDate:
				showDialog(DATE_DIALOG_ID);
				break;
			case R.id.buttonSelectTime:
				showDialog(TIME_DIALOG_ID);
				Toast.makeText(getApplicationContext(),"TIME_DIALOG_ID",
						   Toast.LENGTH_LONG).show();
				Log.d("TIME_DIALOG_ID", "Click");
				break;
			case R.id.getQuoteButton:
				new getQuote().execute();
				Toast.makeText(getApplicationContext(),"Click",
						   Toast.LENGTH_LONG).show();
				Log.d("getQuoteButton", "Click");
				break;
		}
	}
	
	// Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListener =
                           new DatePickerDialog.OnDateSetListener() {
       // the callback received when the user "sets" the Date in the DatePickerDialog
    	@Override                       
    	public void onDateSet(DatePicker view, int yearSelected,
               int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;
             // Set the Selected Date in Select date Button
            bookTaxiDate.setText("Date selected : "+day+"-"+(month+1)+"-"+year);
        }
    };

      // Register  TimePickerDialog listener                 
     private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                               new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
            minute = min;
            bookTaxiTime.setText("Time selected :"+hour+"-"+minute);
          }
	};
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        	case DATE_DIALOG_ID:
        		return new DatePickerDialog(this,mDateSetListener,
                        mYear, mMonth, mDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,mTimeSetListener, 
                		mHour, mMinute, false);
         }
         return null;
   }
	
	class getQuote extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Booktaxi.this);
			pDialog.setMessage("Sending Information...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			
			Log.d("Function", "doInBackgroundStart");
			final Calendar calendar = Calendar.getInstance();
			calendar.set(year,month,day,hour,minute,second);
			long startTime = calendar.getTimeInMillis()/1000;
			sharedpreferences=getSharedPreferences(MyPREFERENCES, 
  			      Context.MODE_PRIVATE);
			String userId=sharedpreferences.getString("userId", "");
			String pickUpFromValue=pickUpFrom.getText().toString();
			String dropOffToValue=dropOffTo.getText().toString();
			String bookTaxiCommentValue=bookTaxiComment.getText().toString();
			String pickupTimestamp = Long.toString(startTime);
			int noOfPassengers=bookTaxiPassengers.getValue();
			int noOfLuggage=bookTaxiLuggage.getValue();
			
			UserFunctions userFunction = new UserFunctions();
			
			JSONObject json = userFunction.getQuote(userId,pickUpFromValue,
					dropOffToValue,bookTaxiCommentValue,pickupTimestamp,
					noOfPassengers,noOfLuggage);
			
			// check for login response
			try {
				if(json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						// Launch Dashboard Screen
						Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
						
						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);
						finish();
					} else {
						pDialog.dismiss();
						Log.d("KEY_ERROR",json.getString(KEY_ERROR_MSG));
						Toast.makeText(getApplicationContext(),json.getString("Service Down=>"+KEY_ERROR_MSG),
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
	/* ######################    Auto Fill Api Code   ################# */
	
	
}
