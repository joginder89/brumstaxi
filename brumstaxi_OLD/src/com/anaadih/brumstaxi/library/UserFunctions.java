package com.anaadih.brumstaxi.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class UserFunctions {
	
	private JSONParser jsonParser;
	
	//String URL = "http://10.0.2.2/gcmtest/index.php";
	private static String loginURL = "http://hrm.testserver87.com/brumstaxi/index.php";
	private static String getQuoteURL = "http://hrm.testserver87.com/brumstaxi/getquote.php";
	private static String fetchQuoteDataURL = "http://hrm.testserver87.com/brumstaxi/fetchquotedata.php";
	private static String fetchJourneyURL = "http://hrm.testserver87.com/brumstaxi/getmyjourneydata.php";
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String USERNAME, String PASSWORD){
		// Building Parameters
		//JSONObject json =null;
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("tag", login_tag));
        nameValuePairs.add(new BasicNameValuePair("username", USERNAME));
        nameValuePairs.add(new BasicNameValuePair("password", PASSWORD));  
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, nameValuePairs);
		Log.d("JSON", json.toString());
		return json;
	}
	
	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String mobile, String username,
			String password,String registrationId,String email ){
		// Building Parameters name, mobile,username,password
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("registrationId", registrationId));
		params.add(new BasicNameValuePair("email", email));
		
		Log.d("BeforeSending", params.toString());
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		return json;
	}
	
	public JSONObject getQuote(String userId,String pickUpFromValue, String dropOffToValue, 
			String bookTaxiCommentValue,String pickupTimestamp,
			String noOfPassengers,String noOfLuggage) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("pickUpFromValue", pickUpFromValue));
		params.add(new BasicNameValuePair("dropOffToValue", dropOffToValue));
		params.add(new BasicNameValuePair("bookTaxiComment", bookTaxiCommentValue));
		params.add(new BasicNameValuePair("pickupTimestamp", pickupTimestamp));
		params.add(new BasicNameValuePair("noOfPassengers", noOfPassengers));
		params.add(new BasicNameValuePair("noOfLuggage", noOfLuggage));
		
		// getting JSON Object
		
		JSONObject json = jsonParser.getJSONFromUrl(getQuoteURL, params);
		return json;
	}
	
	public JSONObject fetchQuoteData(String userRequestId){
		// Building Parameters
		//JSONObject json =null;
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
        nameValuePairs.add(new BasicNameValuePair("userRequestId", userRequestId));  
		JSONObject json = jsonParser.getJSONFromUrl(fetchQuoteDataURL, nameValuePairs);
		Log.d("fetchQuoteDataJSON=>", json.toString());
		return json;
	}
	
	public JSONObject getMyJourneyData(String userId) {
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
        nameValuePairs.add(new BasicNameValuePair("userId", userId));  
		
        JSONObject json = jsonParser.getJSONFromUrl(fetchJourneyURL, 
        		nameValuePairs);
		Log.d("fetchedJourneyJSON=>", json.toString());
		return json;
	}
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	/* Function Used to Find 
	 *   Internet Connection
	 *  State
	 */
}
