package com.buramtexi.driver;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.brumstaxi.driver.dto.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Driversjourneysdetail extends Activity implements OnClickListener {

	static final String TAG = "driverdata";
	TextView from, to, rate;
	ServerRequest request;
	Button conform, pickup, noshow, main, next;
	JSONArray driverData;
	int resultcount = 0;
	String status,companyrequestId;
	
	
	private ProgressDialog pDialog;
	User driverdata;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driversjourneysdetails);
		from = (TextView) findViewById(R.id.pickfrom);
		to = (TextView) findViewById(R.id.dropoff);
		rate = (TextView) findViewById(R.id.rate);
		conform = (Button) findViewById(R.id.conformarrival);
		pickup = (Button) findViewById(R.id.pickedup);
		noshow = (Button) findViewById(R.id.notshow);
	
		main = (Button) findViewById(R.id.main);
		next = (Button) findViewById(R.id.next);

		main.setOnClickListener(this);
		from.setOnClickListener(this);
		to.setOnClickListener(this);
		rate.setOnClickListener(this);
		conform.setOnClickListener(this);
		pickup.setOnClickListener(this);
		noshow.setOnClickListener(this);
		next.setOnClickListener(this);
		// 1. get passed intent
		Intent intent = getIntent();
		// 2. get message value from intent
		
		// 4. get bundle from intent
		
		// 5. get status value from bundle
		driverdata= (User) intent.getSerializableExtra("driverdata");
		// 6. show status on Toast
		
		displayjobs();
		

	}

	// display jobs for driver
	public void displayjobs() {
		
		
		if (driverdata!=null) {
			from.setText(driverdata.getUser_request_from_name());
			to.setText(driverdata.getUser_request_to_name());
			rate.setText("$"+driverdata.getUser_company_fair_ammount());
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {

		case R.id.conformarrival:
			//companyrequestId=company_request_id.get(pos);
			companyrequestId=driverdata.getUser_company_request_id();
			status=	"3";
			//Log.i(TAG, "pos id for update"+pos);
			//Log.i(TAG, "status"+status);
			new updatecustomerstatus().execute();
			break;
		case R.id.pickedup:
			companyrequestId=driverdata.getUser_company_request_id();
			status=	"4";
			Log.i(TAG, "company id"+companyrequestId);
			Log.i(TAG, "status"+status);
			new updatecustomerstatus().execute();
			//Toast.makeText(Driversjourneysdetail.this, "picked successfully...", Toast.LENGTH_SHORT).show();
			break;
		case R.id.notshow:
			companyrequestId=driverdata.getUser_company_request_id();
			status=	"5";
			Log.i(TAG, "company id"+companyrequestId);
			Log.i(TAG, "status"+status);
			new updatecustomerstatus().execute();
			//Toast.makeText(Driversjourneysdetail.this, "not show successfully..", Toast.LENGTH_SHORT).show();
			break;
		case R.id.main:
			Intent intent1 = new Intent(Driversjourneysdetail.this,MainDriver.class);
			startActivity(intent1);
			break;
		case R.id.next:
			/*pos+=1;
			displayjobs(pos);*/
			break;

		}

	}
	
	// update customer status by driver 
				public class updatecustomerstatus extends AsyncTask<String, Integer, String>{

				protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(Driversjourneysdetail.this);
					pDialog.setMessage("Updating Status...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
					request = new ServerRequest();
				}
				
				@Override
				protected String doInBackground(String... params) {
					// TODO Auto-generated method stub
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type","update"));
			        nameValuePairs.add(new BasicNameValuePair("companyrequestId",companyrequestId));
			        nameValuePairs.add(new BasicNameValuePair("status",status));
					String data = request.getDataFromServer(ServerRequest.updatecustomerstatus,nameValuePairs);
					//loginLabel.setText(data);
					return data;
				}
				
				
				protected void onProgressUpdate(Integer... progress) {
					
				}
				protected void onPostExecute(String result){
					pDialog.dismiss();
				}
			}
			
			
			

}
