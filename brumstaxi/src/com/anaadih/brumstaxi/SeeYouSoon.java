package com.anaadih.brumstaxi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SeeYouSoon extends Activity {
	
	String from;
	String to;
	
	TextView textView_from,textView_drop_at;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.see_you_soon);
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
		textView_from.setText(from);
		textView_drop_at.setText(to);
	}
	
	public void Initializer() {
		textView_from = (TextView) findViewById(R.id.textView_from);
		textView_drop_at = (TextView) findViewById(R.id.textView_drop_at);
	}
}
