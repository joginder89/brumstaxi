package com.anaadih.brumstaxi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DriverAtPickupPoint extends Activity {
	
	String from;
	String to;
	
	TextView textView_drop_at;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_atpickup_point);
		Log.d("DriverAtPickupPoint","Call");
		Initializer();
		if(getIntent().getExtras().getString("from") != null) {
			from = getIntent().getExtras().getString("from");
		} else {
			from = "Intent get Null";
		}
		if(getIntent().getExtras().getString("to") != null ) {
			to = getIntent().getExtras().getString("to");
		} 
		else {
			to = "Intent get Null";
		}
		Log.d("DriverAtPickupPoint==>",from+"=="+to);
		textView_drop_at.setText(to);
	}
	
	public void Initializer() {
		textView_drop_at = (TextView) findViewById(R.id.textView_drop_at);
	}
}
