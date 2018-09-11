package flightapp;

import java.io.Serializable;

/**
 * A class that represents a User for an application.
 * A User has a username and a password.
 */
public abstract class User implements Serializable {

	/** 
	 * serialVersionUID generated serial identifier.
	 */
	private static final long serialVersionUID = 7384298488461488078L;
	private String username;
	private String password;

	
	/**
	 * Initializes an instance of User.
	 * 
	 * @param username A string that represents the username of User.
	 * @param password A string that represents the password of User.
	 */
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Initializes an instance of User. This no argument constructor
	 * is needed for serialization.
	 * 
	 */
	public User(){
		this.username = "";
		this.password = "";
	}
	
	/**
	 * Returns a string which is the username of User.
	 * 
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Sets username of User to a given string.
	 * 
	 * @param username the username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Returns a string which is the password for User.
	 * 
	 * @return the password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Sets password to a given string for user.
	 * 
	 * @param password the password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
