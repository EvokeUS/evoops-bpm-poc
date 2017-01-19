package com.evoke.researchlabs.row.hr.domain;

/**
 * POJO for passing Bonita credentials
 * @author bmohammad
 *
 */
public class Credentials {
	private String username;
	private String password;
	
	public Credentials(){
		//public constructor
	}
	
	public Credentials(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Credentials [username=" + username + ", password=" + password + "]";
	}
}
