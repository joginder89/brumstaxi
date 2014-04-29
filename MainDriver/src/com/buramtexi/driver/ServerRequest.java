package com.buramtexi.driver;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;


public class ServerRequest {
	
	public static String path = "http://hrm.testserver87.com/brumstaxidriver/";
	//public static String path = "http://10.0.2.2/brumsTaxiDriverPhp/";

	public static String pathgcm = path+"drivergcm.php";
	public static String pathdriverdata =  path+"driverdata.php";
	public static String pathLogin = path + "driverlogin.php";
	public static String updatecustomerstatus = path + "updatecustomerdriver.php";
	
	public String getDataFromServer(String url , List<NameValuePair> nameValuePairs ) {
		
		//ConnectionDetector cd = new ConnectionDetector();		�
		String responseText = null;
			// Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(url);

		    try {
		        // Add your data
		        /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));*/
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        responseText = EntityUtils.toString(entity);
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	System.out.println("Hello"+e);
		    }

		   // Log.d("Responce", responseText);
				
	   
	    return responseText;
	} 
	
public String getDataFromServer(String url) {
		
		String responseText = null;
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	     	// Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        responseText = EntityUtils.toString(entity);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }

	    Log.d("Responce", responseText);
	    return responseText;
	} 

public boolean isConnectingToInternet(Context _context){
    ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivity != null) 
      {
          NetworkInfo[] info = connectivity.getAllNetworkInfo();
          if (info != null) 
              for (int i = 0; i < info.length; i++) 
                  if (info[i].getState() == NetworkInfo.State.CONNECTED)
                  {
                      return true;
                  }

      }
      return false;
}


}
