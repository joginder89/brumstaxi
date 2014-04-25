package com.buramtexi.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brumstaxi.driver.dto.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainDriver extends Activity  {

	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context context;
    GoogleCloudMessaging gcm;
    String regid;
	SharedPreferences preferences ;
	String SENDER_ID = "630771439798";
	String driverid;
	JSONArray driverData;
	ServerRequest request;
	TextView pickfrom,pickto,rate,pickfrom1,pickto1,rate1;
	Button customerdetails,mainscreen,nextbutton,viewjob1,viewjob2;
	View mainviewgroup,secondviewgroup;
	int posset=0;
	int resultcount=0;
	private ProgressDialog pDialog;
	static final String TAG = "GCMDemo";
	ArrayList<User> users;
	ListView listview_journeyblock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		setContentView(R.layout.maindriver);
		listview_journeyblock=(ListView) findViewById(R.id.listview_journeyblock);
		
		
		
		//create gcm 
		 
		// Check device for Play Services APK. If check succeeds, proceed with
	        //  GCM registration.
	        if (checkPlayServices()) {
	        	 gcm = GoogleCloudMessaging.getInstance(this);
	            
	             regid = getRegistrationId(context);
	            
	            if (regid.isEmpty()) {
	            	//Toast.makeText(MainDriver.this, "empty regid",Toast.LENGTH_SHORT).show();
	                registerInBackground();
	            }
	        } else {
	            Log.i(TAG, "No valid Google Play Services APK found.");
	        }
		
		preferences = getSharedPreferences("driver.data", Context.MODE_PRIVATE);
		//preferences.edit().clear().commit();
		String showdata=preferences.getString("name", "coundnot");
		driverid=preferences.getString("id", "no driver id");
		if(showdata.equalsIgnoreCase("coundnot")){
			Intent i = new Intent(MainDriver.this, Login.class);
			startActivityForResult(i, 2);	
		}else{
		new loaddriverdata().execute();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // TODO Auto-generated method stub
	    super.onActivityResult(requestCode, resultCode, data);
	    // check if the request code is same as what is passed  here it is 2  
        	if(requestCode==2)  
              {  
        		driverid =data.getStringExtra("returnKey1");   
                 //Toast.makeText(MainDriver.this, driverid, Toast.LENGTH_LONG).show();  
        		new insertgcm().execute();
        		new loaddriverdata().execute();
              }else{
            	  super.finish();
              }  
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_driver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_driver,
					container, false);
			return rootView;
		}
	}
	
	

	//all functions for gcm
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
	
	 private String getRegistrationId(Context context) {
	        final SharedPreferences prefs = getGCMPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
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
	
	 
	 /**
		 * @return Application's {@code SharedPreferences}.
		 */
		private SharedPreferences getGCMPreferences(Context context) {
		    // This sample app persists the registration ID in shared preferences, but
		    // how you store the regID in your app is up to you.
		    return getSharedPreferences(MainDriver.class.getSimpleName(),
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
		
		
		private void registerInBackground() {
		    new AsyncTask<String, String, String>() {

		    	@Override
		        protected String doInBackground(String... params) {
		            String msg = "";
		            try {
		                if (gcm == null) {
		                	gcm = GoogleCloudMessaging.getInstance(context);
		                	//Log.i(TAG, "regid check"+gcm);
		                }	
		                regid = gcm.register(SENDER_ID);
			            //Log.i(TAG, "regid check"+regid);
			            msg = "Device registered, registration ID=" + regid;
			            storeRegistrationId(context, regid);
			            Log.i(TAG, "after storeRegistrationId"+msg);
		            } catch (IOException ex) {
		                msg = "Error :" + ex.getMessage();
		                Log.i(TAG, "error expection"+msg);
		                // If there is an error, don't just keep trying to register.
		                // Require the user to click a button again, or perform
		                // exponential back-off.
		            }
		            Log.i(TAG, "error check"+msg);
		            return msg;
		        }

		        @Override
		        protected void onPostExecute(String msg) {
		           // mDisplay.append(msg + "\n");
		        	 //Log.i(TAG, "message"+msg);
		        	//Toast.makeText(MainDriver.this, msg,Toast.LENGTH_SHORT).show();
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
			Log.i(TAG, "inset storeRegistrationId"+regId);
		    final SharedPreferences prefs = getGCMPreferences(context);
		    int appVersion = getAppVersion(context);
		    Log.i(TAG, "SharedPreferences");
		    Log.i(TAG, "Saving regId on app version " + appVersion);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString(PROPERTY_REG_ID, regId);
		    editor.putInt(PROPERTY_APP_VERSION, appVersion);
		    editor.commit();
		    Log.i(TAG, "commit");
		}
		
		//end gcm functions

	//insert gcm of driver
		public class insertgcm extends AsyncTask<String, Integer, String>{

			protected void onPreExecute() {
				super.onPreExecute();
				request = new ServerRequest();
			}
			
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("type","insert"));
		        nameValuePairs.add(new BasicNameValuePair("driverid",driverid));
		        nameValuePairs.add(new BasicNameValuePair("gcm",regid));
				String data = request.getDataFromServer(ServerRequest.pathgcm,nameValuePairs);
				//loginLabel.setText(data);
				
				return data;
			}
			
			
			protected void onProgressUpdate(Integer... progress) {
				
			}
			protected void onPostExecute(String result){
				
			}
		}
		
		
		
		//Load driver dada for pick servers
				public class loaddriverdata extends AsyncTask<String, Integer, String>{

					protected void onPreExecute() {
						super.onPreExecute();
						pDialog = new ProgressDialog(MainDriver.this);
						pDialog.setMessage("Fetching Data...");
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(true);
						pDialog.show();
						request = new ServerRequest();
					}
					
					@Override
					protected String doInBackground(String... params) {
						// TODO Auto-generated method stub
						
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("type","fetch"));
				        nameValuePairs.add(new BasicNameValuePair("driverid",driverid));
				       // Log.i(TAG, "do in backend location"+driverid);
				        String driverdata=null;
				        if(request.isConnectingToInternet(context)){
				        	
				        	 driverdata= request.getDataFromServer(ServerRequest.pathdriverdata,nameValuePairs);
						       
						      
			            }
				        return driverdata;
					}
					
					
					protected void onProgressUpdate(Integer... progress) {
						
						
					}
					protected void onPostExecute(String result){
						if(pDialog!=null&&pDialog.isShowing()){
							pDialog.dismiss();
						}
						
						Log.i(TAG, "result"+result);
						//String str = null;
						if(request.isConnectingToInternet(context) && result != null){
							//Log.i(TAG, "get the data "+result);
							try {
								users=new ArrayList<User>();
								driverData = new JSONArray(result);
								
								if(driverData.getJSONObject(0).getString("company_id").toString() != "-1"){
								resultcount=driverData.length();
								for(int i = 0 ; i < driverData.length();i++)
								{
									User user=new User();
									JSONObject jsonObject=	driverData.getJSONObject(i);
									user.setUser_comapny_id(jsonObject.getString("company_id"));
									user.setUser_company_request_id(jsonObject.getString("company_request_id"));
									user.setUser_company_fair_ammount(jsonObject.getString("company_fare_amount"));
									user.setUser_id(jsonObject.getString("user_id"));
									user.setUser_request_from_name(jsonObject.getString("user_request_from_name"));
									user.setUser_request_to_name(jsonObject.getString("user_request_to_name"));
									user.setUser_request_time(jsonObject.getString("user_request_time"));
									user.setUser_request_date(jsonObject.getString("user_request_date"));
									users.add(user);
								}
								if(users!=null&&users.size()>0){
									CreateMyJourneyBlock journeyBlock=new CreateMyJourneyBlock();
									listview_journeyblock.setAdapter(journeyBlock);
									listview_journeyblock.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(MainDriver.this,Driversjourneysdetail.class);
				                              
				                           intent.putExtra("pos",Integer.toString(posset));
				                           intent.putExtra("driverdata", users.get(position));
				                     
				                            startActivity(intent);
				 
										}
									});
								}
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
			            }else{
			        		//Toast.makeText(MainDriver.this, "Not Connected to Internet !!!", Toast.LENGTH_LONG).show();	
			        		
			        		AlertDialog.Builder builder = new AlertDialog.Builder(MainDriver.this);
			                builder.setTitle("Attention!");
			                builder.setMessage("Connection to Server failed.");
			                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

			                public void onClick(DialogInterface dialog, int which) {
			                	finish();
			                }
			                });
			                AlertDialog dialog = builder.create();
			                dialog.show();
			            }
						
					}
				}
		
		
				
		//display jobs for driver
		

		
		
		
		
		private class CreateMyJourneyBlock extends BaseAdapter {

			@Override
			public int getCount() {
				Log.e("CreateMyJourneyBlock Adapter called", "count ");
				return users.size();
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = getLayoutInflater();
				JSONObject jsonObject;
				View row;
	            row = inflater.inflate(R.layout.myjourney_block, parent, false);
	            TextView pickfrom, dropoff,rate;
	            
	            pickfrom = (TextView) row.findViewById(R.id.pickfrom);
	            dropoff = (TextView) row.findViewById(R.id.dropoff);
	            rate = (TextView) row.findViewById(R.id.rate);
	            
				try {
					
					pickfrom.setText(users.get(position).getUser_request_from_name());
					dropoff.setText(users.get(position).getUser_request_to_name());
					rate.setText("$"+users.get(position).getUser_company_fair_ammount());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.e("MyJourney", "view ");
	            return (row);
			}
		}
				
}