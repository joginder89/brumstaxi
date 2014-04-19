package com.anaadih.brumstaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class RegistrationResponse extends Activity {
	
	private Handler mHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_response);
		
		mHandler.postDelayed(new Runnable() {
            public void run() {
            	Intent dashboard = new Intent(RegistrationResponse.this,MainActivity.class);
        		
        		// Close all views before launching Dashboard
        		dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		startActivity(dashboard);
        		
        		// Close this Screen
        		finish();
            }
        }, 3000);
	}
}