package flightapp;

import java.util.ArrayList;

/**
 * A class that represents an administrator of a flight application.
 *
 */
public class Admin extends Client{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7947488324717806593L;

	/**
	 * Initializes an instance of Admin.
	 * 
	 * @param username A string which is the username of the admin.
	 * @param password A string which is the password of the admin.
	 * @param firstName A string that represents the first name.
	 * @param lastName A string that represents the last name.
	 * @param address A string that represents the address.
	 * @param email A string that represents the email.
	 * @param creditCardNumber A string which is the credit card .
	 * number of the user.
	 * @param expirationDate A string which is the expiration date of the.
	 * admin's credit card.
	 * 
	 */
	public Admin(String username, String password, String lastName
			, String firstName, String email, String address
			, String creditCardNumber, String expirationDate) {
		super(username, password, lastName, firstName, email,
                address, creditCardNumber, expirationDate);
		
	}

	/**
	 * Initializes an instance of Admin.
	 *
	 * @param username A string that represents the username of Admin.
	 * @param password A string that represents the password of Admin.
	 */
	public Admin(String username, String password){
		super(username, password);
	}

	/**
	 * Initializes an instance of Admin.
	 *
	 * @param username A string which is the username of the admin.
	 * @param password A string which is the password of the admin.
	 * @param firstName A string that represents the first name.
	 * @param lastName A string that represents the last name.
	 * @param address A string that represents the address.
	 * @param email A string that represents the email.
	 * @param creditCardNumber A string which is the credit card .
	 * number of the user.
	 * @param expirationDate A string which is the expiration date of the.
	 * admin's credit card.
	 * @param booking An ArrayList of Booking which represents the client's
	 * bookings
	 */
	public Admin(String username, String password, String lastName
			, String firstName, String email, String address
			, String creditCardNumber, String expirationDate
			, ArrayList<Booking> booking) {
		super(username, password, lastName, firstName, email,
				address, creditCardNumber, expirationDate, booking);

	}

	/**
	 * Changes the given client's username to a given one.
	 * 
	 * @param c The Client that is being edited.
	 * @param username A string which is the new username of the client.
	 */
	public void editClientUsername(Client c, String username){
		c.setUsername(username);
	}
	
	/**
	 * Changes the given client's password to a given one.
	 * 
	 * @param c The Client that is being edited.
	 * @param password A string which is the new password of the client.
	 */
	public void editClientPassword(Client c, String password){
		c.setPassword(password);
	}
	
	/**
	 * Changes the given client's credit card number to a given one.
	 * 
	 * @param c The Client that is being edited.
	 * @param cardNum A string which is the new credit card number of the client.
	 */
	public void editClientCreditCardNumber(Client c, String cardNum){
		c.setCreditCardNumber(cardNum);
	}
	
	/**
	 * Changes the given client's email to a given one.
	 * 
	 * @param c The Client that is being edited.
	 * @param email A string which is the new email of the client.
	 */
	public void editClientEmail(Client c, String email){
		c.setEmail(email);
	}
	
	/**
	 * Changes the given client's first name to a given one.
	 * 
	 * @param c The Client that is being editted.
	 * @param fName A string which is the new first name of the client.
	 */
	public void editClientFirstName(Client c, String fName){
		c.setFirstName(fName);
	}
	
	/**
	 * Changes the given client's last name to a given one.
	 * 
	 * @param c The Client that is being editted.
	 * @param lName A string which is the new last name of the client.
	 */
	public void editClientLastName(Client c, String lName){
		c.setLastName(lName);
	}
	
	/**
	 * Changes the given client's address to a given one.
	 * 
	 * @param c The Client that is being editted.
	 * @param address A string which is the new address of the client.
	 */
	public void editClientAddress(Client c, String address){
		c.setAddress(address);
	}
	
	/**
	 * Changes the given client's credit card's expiration date to a given one.
	 * 
	 * @param c The Client that is being editted.
	 * @param expirationDate A string which is the new expiration date of the client's credit card.
	 */
	public void editClientExpirationDate(Client c, String expirationDate){
		c.setExpirationDate(expirationDate);
	}
	
	/**
	 * Books an Itinerary for a given Client
	 * 
	 * @param c The Client that is being editted.
	 * @param flights An Itinerary that will be added to Client's bookings.
	 */
	public void bookItinerary(Client c, Itinerary flights){
		ArrayList<Booking> bookings = (ArrayList<Booking>) c.getBooking();
		bookings.add(new Booking(c.getUsername(), flights));
		c.setBooking(bookings);
	}
	
	/**
	 * Changes the given flight's number to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param flightNumber An integer that is the new flight number for flight.
	 */
	public void editFlightNumber(Flight f, int flightNumber){
		f.setFlightNumber(flightNumber);
	}
	
	/**
	 * Changes the given flight's departure to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param departure A string that is the new departure for flight.
	 */
	public void editFlightDeparture(Flight f, String departure){
		f.setDeparture(departure);
	}
	
	/**
	 * Changes the given flight's arrival to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param arrival A string that is the new arrival for flight.
	 */
	public void editFlightArrival(Flight f, String arrival){
		f.setArrival(arrival);
	}
	
	/**
	 * Changes the given flight's airline to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param airline A string that is the new airline for flight.
	 */
	public void editFlightAirline(Flight f, String airline){
		f.setAirline(airline);
	}
	
	/**
	 * Changes the given flight's origin to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param origin A string that is the new origin for flight.
	 */
	public void editFlightOrigin(Flight f, String origin){
		f.setOrigin(origin);
	}
	
	/**
	 * Changes the given flight's destination to a given one.
	 * 
	 * @param f The Flight that is being edited.
	 * @param destination A string that is the new destination for flight.
	 */
	public void editFlightDestination(Flight f, String destination){
		f.setDestination(destination);
	}
	
	/**
	 * Changes the given flight's price to a given one.
	 * 
	 * @param f The Flight that is being editted.
	 * @param price A double that is the new price for flight.
	 */
	public void editFlightPrice(Flight f, Double price){
		f.setPrice(price);
	}
}
