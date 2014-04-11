package com.anaadih.brumstaxi;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
public class Quote extends Activity {

	  	TextView myid;
	    TextView deal;
	    TextView valid;
	    TextView address;
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
			String rowid = json.getString("rowid");
			myid.setText(rowid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}