package com.anaadih.brumstaxi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.anaadih.brumstaxi.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Button registrationButton,LoginButton, bookATaxiButton, myJourneysButton, 
    	futureJourneyButton,callUsButton;
    public static final String MyPREFERENCES = "BrumsTaxiPrefs" ;
    public String userId;
    SharedPreferences sharedpreferences;
    LinearLayout contentBody;
    
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "18282624851";

    /**
     * Substitute you own URL SERVER. In your server you can save the register id to
     * send notification via GCM
     */
    	//String URL = "http://android.bhaiwah.com/saveid.php";
    	
    	String URL = "http://10.0.2.2/gcmtest/saveid.php";
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "BrumsTaxi";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
       
        mDisplay = (TextView) findViewById(R.id.tvNeedTaxi);
        mDisplay.setTextColor(Color.YELLOW);
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            Log.i(TAG, "Google Play Services APK found");
            sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
            String refUserId=sharedpreferences.getString("userId", "");
            
            gcm = GoogleCloudMessaging.getInstance(this);
            
            regid = getRegistrationId(context);
            //String msg1="Already Register==>"+regid;
            //mDisplay.append(msg1 + "\n");
            if (regid.isEmpty()) {
                registerInBackground();
            }
            Initilizer();
            registrationButton.setOnClickListener(this);
            LoginButton.setOnClickListener(this);
            bookATaxiButton.setOnClickListener(this);
            myJourneysButton.setOnClickListener(this);
            futureJourneyButton.setOnClickListener(this);
            callUsButton.setOnClickListener(this);
    		
    		if(refUserId != "") {
    			//registrationButton.setVisibility(View.INVISIBLE);
    			//LoginButton.setVisibility(View.VISIBLE);
    			contentBody.removeView(registrationButton);	
    		}
    		else {
    			contentBody.removeView(LoginButton);
    		}
            
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }
    
    private void Initilizer() {
		// TODO Auto-generated method stub
    	contentBody = (LinearLayout)findViewById(R.id.contentBody);
    	registrationButton = (Button)findViewById(R.id.registrationButton);
    	LoginButton = (Button)findViewById(R.id.LoginButton);
    	bookATaxiButton = (Button)findViewById(R.id.bookATaxiButton);
    	myJourneysButton = (Button)findViewById(R.id.myJourneysButton);
    	futureJourneyButton = (Button)findViewById(R.id.futureJourneyButton);
    	callUsButton = (Button)findViewById(R.id.callUsButton);
	}
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(regid != null) {
	    	switch(v.getId()){
				case R.id.bookATaxiButton:
					Intent intent = new Intent(MainActivity.this,
							Booktaxi.class);
			         startActivity(intent);
					break;
				case R.id.registrationButton:
					goToRegisterPage();
					break;
				case R.id.LoginButton:
					goToLoginPage();
					break;
				case R.id.futureJourneyButton:
					goToFutureJourneyPage();
					break;
			}
		}
		else {
			Toast.makeText(getApplicationContext(), 
					"Please wait Reg. in Progress",
					   Toast.LENGTH_LONG).show();
		}
	}
    
    public void goToRegisterPage() {
    	Intent intent = new Intent(MainActivity.this,Register.class);
         startActivity(intent);
	}
    
    public void goToLoginPage() {
    	Intent intent = new Intent(MainActivity.this,Login.class);
         startActivity(intent);
	}
    
    public void goToFutureJourneyPage() {
    	Intent intent = new Intent(this,Quote.class);
         startActivity(intent);
	}
    
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Device is not Registered.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported for GooglePlayServicesUtil.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	private void registerInBackground() {
	    new AsyncTask<String, String, String>() {

	    	@Override
	        protected String doInBackground(String... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }	                
		            regid = gcm.register(SENDER_ID);
		            msg = "Device registered, registration ID=" + regid;
		            sendRegistrationIdToBackend();
		            storeRegistrationId(context, regid);
	               
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            //mDisplay.append(msg + "\n");
	        }
	    }.execute(null, null, null);
	    
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.URL);

	    try {
	    	
	    	String email = UserInfo.getEmail(context);
	    	
	    	
	    	if(email == null) {
	    		email="user not register with Google";
	    	}
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("email", email));
	        nameValuePairs.add(new BasicNameValuePair("city", "not given"));
	        nameValuePairs.add(new BasicNameValuePair("token", this.regid));
	        nameValuePairs.add(new BasicNameValuePair("device", "android"));

	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
		
	}
}
