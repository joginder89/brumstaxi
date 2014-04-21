package com.anaadih.brumstaxi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ConfirmFare extends Activity{
	
	TextView company_txt,price_txt;
	String price,comapny;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_fare);
		comapny = getIntent().getStringExtra("company");
		price = getIntent().getStringExtra("price");
		 Log.e("Quote", "Second"+comapny+price);
		company_txt=(TextView) findViewById(R.id.textView_company);
		price_txt=(TextView) findViewById(R.id.textView_fair);
		company_txt.setText(comapny);
		price_txt.setText(price);
		/*
		int companyRequestId = bundle.getInt("company_request_id", 0);
		Initilizer();
		
		test.setText(companyRequestId);*/
	}
	
	void Initilizer() {
		
		//test = (TextView) findViewById(R.id.test);
	}
}