package com.anaadih.brumstaxi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DriverAtPickupPoint extends Activity {
	
	String driveratpickuppoint;
	TextView test2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_atpickup_point);
		Log.d("DriverAtPickupPoint","Call");
		Initializer();
		if(getIntent().getExtras().getString("driveratpickuppoint") != null) {
			driveratpickuppoint = getIntent().getExtras().getString("driveratpickuppoint");
		} else {
			driveratpickuppoint = "Intent get Null";
		}
		Log.d("DriverAtPickupPoint==>",driveratpickuppoint);
		test2.setText(driveratpickuppoint);
	}
	
	public void Initializer() {
		test2 = (TextView) findViewById(R.id.test2);
	}
}
