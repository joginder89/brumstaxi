package com.brumstaxi.driver.dto;

import java.io.Serializable;

public class User  implements Serializable{
	private String user_comapny_id;
	private String user_company_request_id;
	private String user_company_fair_ammount;
	private String user_id;
	private String user_request_from_name;
	private String user_request_to_name;
	private String user_request_date;
	private String user_request_time;
	public String getUser_comapny_id() {
		return user_comapny_id;
	}
	public void setUser_comapny_id(String user_comapny_id) {
		this.user_comapny_id = user_comapny_id;
	}
	public String getUser_company_request_id() {
		return user_company_request_id;
	}
	public void setUser_company_request_id(String user_company_request_id) {
		this.user_company_request_id = user_company_request_id;
	}
	public String getUser_company_fair_ammount() {
		return user_company_fair_ammount;
	}
	public void setUser_company_fair_ammount(String user_company_fair_ammount) {
		this.user_company_fair_ammount = user_company_fair_ammount;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_request_from_name() {
		return user_request_from_name;
	}
	public void setUser_request_from_name(String user_request_from_name) {
		this.user_request_from_name = user_request_from_name;
	}
	public String getUser_request_to_name() {
		return user_request_to_name;
	}
	public void setUser_request_to_name(String user_request_to_name) {
		this.user_request_to_name = user_request_to_name;
	}
	public String getUser_request_date() {
		return user_request_date;
	}
	public void setUser_request_date(String user_request_date) {
		this.user_request_date = user_request_date;
	}
	public String getUser_request_time() {
		return user_request_time;
	}
	public void setUser_request_time(String user_request_time) {
		this.user_request_time = user_request_time;
	}
	

}
