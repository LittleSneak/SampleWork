/**
 * 
 */
package flightapp;

import java.util.List;
import java.util.ArrayList;

/**
 * A class that represents a client for a flight application.
 * This class extends User.
 *
 */
public class Client extends User implements java.io.Serializable{
	
	/**
	 *  serialVersionUID generated unique serial version identifier.
	 */
	private static final long serialVersionUID = 3583462146320402409L;
	private List<Booking> booking;
	private String creditCardNumber;
	private String expirationDate;
	private String firstName;
	private String lastName;
	private String address;
	private String email;

	/**
	 * Initializes an instance of Client.
	 * 
	 * @param username A string which is the username of the client.
	 * @param password A string which is the password of the client.
	 * @param firstName A string that represents the first name.
	 * @param lastName A string that represents the last name.
	 * @param address A string that represents the address.
	 * @param email A string that represents the email.
	 * @param creditCardNumber A string which is the credit card .
	 * number of the user.
	 * @param expirationDate A string which is the expiration date of the.
	 * user's credit card.
	 * 
	 */
	public Client(String username, String password, String lastName
			, String firstName, String email, String address
			, String creditCardNumber, String expirationDate) {
		super(username, password);
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.address = address;
		this.creditCardNumber = creditCardNumber;
		this.expirationDate = expirationDate;
		
		booking = new ArrayList<Booking>();
	}

	/**
	 * Initializes an instance of Client.
	 *
	 * @param username A string that represents the username of Client.
	 * @param password A string that represents the password of Client.
	 */
	public Client(String username, String password){
		super(username, password);
	}

	/**
	 * Initializes an instance of Client. This constructor is called by Admin.
	 * 
	 * @param username A string which is the username of the client.
	 * @param password A string which is the password of the client.
	 * @param firstName A string that represents the first name.
	 * @param lastName A string that represents the last name.
	 * @param address A string that represents the address.
	 * @param email A string that represents the email.
	 * @param creditCardNumber A string which is the credit card .
	 * number of the user.
	 * @param expirationDate A string which is the expiration date of the.
	 * user's credit card.
	 * @param booking An ArrayList of Booking which represents the client's
	 * bookings
	 * 
	 */
	public Client(String username, String password, String lastName
			, String firstName, String email, String address
			, String creditCardNumber, String expirationDate
			, ArrayList<Booking> booking) {
		super(username, password);
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.address = address;
		this.creditCardNumber = creditCardNumber;
		this.expirationDate = expirationDate;
		this.booking = booking;
	}

	/**
	 * Returns booking of Client.
	 * 
	 * @return the booking.
	 */
	public List<Booking> getBooking() {
		return booking;
	}

	/**
	 * Sets booking to a given ArrayList of Booking.
	 * 
	 * @param booking the booking to set.
	 */
	public void setBooking(List<Booking> booking) {
		this.booking = booking;
	}
	
	/**
	 * Returns creditCardNumber of Client.
	 * 
	 * @return the creditCardNumber.
	 */
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	/**
	 * Sets creditCardNumber to a given string.
	 * 
	 * @param creditCardNumber the creditCardNumber to set.
	 */
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	/**
	 * Returns the expiratioinDate of Client.
	 * 
	 * @return the expirationDate.
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * Sets expirationDate to a given string.
	 * 
	 * @param expirationDate the expirationDate to set.
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Returns firstName of Client.
	 * 
	 * @return the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets firstName to a given string.
	 * 
	 * @param firstName the firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns lastName of Client.
	 * 
	 * @return the lastName.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets lastName to a given string.
	 * 
	 * @param lastName the lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns address of Client.
	 * 
	 * @return the address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets address to a given string.
	 * 
	 * @param address the address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Returns email of Client.
	 * 
	 * @return the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets email to a given string.
	 * 
	 * @param email the email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Returns a string representation of Client.
	 * 
	 * @return A string which represents Client.
	 */
	public String toString(){
		return this.lastName + "," + this.firstName + "," 
				+ this.email + "," + this.address + "," 
				+ this.creditCardNumber + "," + this.expirationDate;
	}
	
	/**
	 * Adds a given Itinerary to booking of Client.
	 * 
	 * @param flights A Itinerary which will be added to booking.
	 */
	public void bookItinerary(Itinerary flights){
		Booking newBooking = new Booking(this.getUsername(),flights);
		this.booking.add(newBooking);
	}

	/**
	 * Returns true if the given itinerary is already booked by client
	 *
	 * @param i An itinerary
	 * @return A boolean which is true if the client has already booked i
	 */
	public boolean isBooked(Itinerary i){
		for (Booking booking : this.booking){
			if(i.equals(booking.getFlights())){
				return true;
			}
		}
		return false;
	}
}
