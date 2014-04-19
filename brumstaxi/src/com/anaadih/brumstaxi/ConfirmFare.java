package com.anaadih.brumstaxi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfirmFare extends Activity{
	
	TextView test;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*Bundle bundle = getIntent().getExtras();
		int companyRequestId = bundle.getInt("company_request_id", 0);
		Initilizer();
		
		test.setText(companyRequestId);*/
	}
	
	void Initilizer() {
		
		//test = (TextView) findViewById(R.id.test);
	}
}