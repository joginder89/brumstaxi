package com.anaadih.brumstaxi;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.anaadih.brumstaxi.library.UserFunctions;
import com.andreabaccega.widget.FormEditText;

import android.R.string;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Booktaxi extends Activity implements View.OnClickListener,OnItemClickListener {
	
	FormEditText bookTaxiComment;
	AutoCompleteTextView pickUpFrom,dropOffTo;
	Button getQuoteButton,callUsButton;
	//Button buttonSelectDate,buttonSelectTime;
	TextView bookTaxiDate,bookTaxiTime;
	TextView bookTaxiPassengers,bookTaxiLuggage;
	LinearLayout layoutSelectDate,layoutSelectTime,lauoutPassengers,layoutLuggage;
	private ProgressDialog pDialog;
	boolean allValid = true;
	public static final String MyPREFERENCES = "BrumsTaxiPrefs" ;
    public String userId;
    SharedPreferences sharedpreferences;
    String noOfPassangers,noOfLuggage;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID=1;
	public  int year,month,day,hour,minute,second=00;  
	public  int yearSelected,monthSelected,daySelected,hourSelected,minuteSelected;
	private int mYear, mMonth, mDay,mHour,mMinute;
	JSONObject jsonResult;
	
	// JSON Response node names
			private static String KEY_SUCCESS = "success";			
	
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
		
		getQuoteButton.setOnClickListener(this);
		callUsButton.setOnClickListener(this);
		
		layoutSelectDate.setOnClickListener(this);
		layoutSelectTime.setOnClickListener(this);
		lauoutPassengers.setOnClickListener(this);
		layoutLuggage.setOnClickListener(this);
		
		if( getIntent().getExtras() != null) {
			String pickUpFromString = getIntent().getExtras().getString("pickUpFromString");
			String dropOffAtString = getIntent().getExtras().getString("dropOffAtString");
			pickUpFrom.setText(pickUpFromString);
			dropOffTo.setText(dropOffAtString);
		}
		
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
		
		/*buttonSelectDate=(Button)findViewById(R.id.buttonSelectDate);
		buttonSelectTime=(Button)findViewById(R.id.buttonSelectTime);*/
		
		getQuoteButton=(Button)findViewById(R.id.getQuoteButton);
		callUsButton=(Button)findViewById(R.id.callUsButton);
		
		bookTaxiDate=(TextView)findViewById(R.id.bookTaxiDate);
		bookTaxiTime=(TextView)findViewById(R.id.bookTaxiTime);
		
		bookTaxiPassengers = (TextView) findViewById(R.id.bookTaxiPassengers);
		bookTaxiLuggage = (TextView) findViewById(R.id.bookTaxiLuggage);
		
		pickUpFrom = (AutoCompleteTextView) findViewById(R.id.pickUpFrom);
		dropOffTo = (AutoCompleteTextView) findViewById(R.id.dropOffTo);
		bookTaxiComment = (FormEditText) findViewById(R.id.bookTaxiComment);
		
		layoutSelectDate = (LinearLayout) findViewById (R.id.layoutSelectDate);
		layoutSelectTime = (LinearLayout) findViewById (R.id.layoutSelectTime);
		lauoutPassengers = (LinearLayout) findViewById (R.id.lauoutPassengers);
		layoutLuggage = (LinearLayout) findViewById (R.id.layoutLuggage);
		
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
			case R.id.layoutSelectDate:
				showDialog(DATE_DIALOG_ID);
				break;
			case R.id.layoutSelectTime:
				showDialog(TIME_DIALOG_ID);
				break;
			case R.id.getQuoteButton:
				new getQuote().execute();
				break;
			case R.id.lauoutPassengers:
				getPassengers();
				break;
			case R.id.layoutLuggage:
				getLuggage();
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
            //bookTaxiDate.setText(""+day+"-"+(month+1)+"-"+year);
            bookTaxiDate.setText(""+day+" "+new DateFormatSymbols().getMonths()[month]);
        }
    };

      // Register  TimePickerDialog listener                 
     private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                               new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
            minute = min;            
            //getTime(hourOfDay,min);
            bookTaxiTime.setText(""+hour+"-"+minute);
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
			int noOfPassengers=Integer.getInteger(noOfPassangers);
			int noOfLuggages=Integer.getInteger(noOfLuggage);
			UserFunctions userFunction = new UserFunctions();
			
			jsonResult = userFunction.getQuote(userId,pickUpFromValue,
					dropOffToValue,bookTaxiCommentValue,pickupTimestamp,
					noOfPassengers,noOfLuggages);
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Log.d("ReceiveResult",jsonResult.toString());
			try {
				if(jsonResult.getString(KEY_SUCCESS) != null) {
					String res = jsonResult.getString(KEY_SUCCESS); 
					if(Integer.parseInt(res) == 1) {
						Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);
						finish();
					} 
				} 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
/*	string getTime(int hrs,int mint) {
		
		String s = hrs+":"+mint+":00";
		final String time = hrs+":"+mint;

		try {
		    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm a");
		    final Date dateObj = sdf.parse(time);
		    System.out.println(dateObj);
		    System.out.println(new SimpleDateFormat("K:mm a").format(dateObj));
		} catch (final ParseException e) {
		    e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	
	}
*/	
	public void getPassengers() {
         final Dialog d = new Dialog(Booktaxi.this);
         d.setTitle("NumberPicker");
         d.setContentView(R.layout.numberpicker_dialog);
         Button b1 = (Button) d.findViewById(R.id.numPickerSetBtn);
         Button b2 = (Button) d.findViewById(R.id.numPickerCancelBtn);
         
         final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
         np.setMaxValue(9);
         np.setMinValue(1);
         np.setWrapSelectorWheel(false);
         //np.setOnValueChangedListener((OnValueChangeListener) Booktaxi.this);
         b1.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  noOfPassangers = String.valueOf(np.getValue());
        	  bookTaxiPassengers.setText(noOfPassangers);
              d.dismiss();
           }    
          });
         b2.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              d.dismiss();
          }    
          });
       d.show();
    }
	
	
	public void getLuggage() {
        final Dialog d = new Dialog(Booktaxi.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.numberpicker_dialog);
        Button b1 = (Button) d.findViewById(R.id.numPickerSetBtn);
        Button b2 = (Button) d.findViewById(R.id.numPickerCancelBtn);
        
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(9);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        //np.setOnValueChangedListener((OnValueChangeListener) Booktaxi.this);
        b1.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
        	 	noOfLuggage = String.valueOf(np.getValue());
        	 	bookTaxiLuggage.setText(noOfLuggage);
       	  		d.dismiss();
          }    
         });
        b2.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
             d.dismiss();
         }    
         });
      d.show();
   }
	
}
