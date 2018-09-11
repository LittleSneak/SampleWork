package flightapp;

import java.io.Serializable;

/**
 * A class that represents flight bookings.
 *
 */
public class Booking implements Serializable {
	
	/**
	 * serialVersionUID Generated serial version unique identifier.
	 */
	private static final long serialVersionUID = -5320126244869455322L;
	private String client;
	private Itinerary flights;
	
	/**
	 * Initializes an instance of Booking.
	 * 
	 * @param client A string which represents the username of the
	 * client who made this booking.
	 * 
	 * @param flights An Itinerary of flights that client has booked.
	 */
	public Booking(String client, Itinerary flights){
		this.client = client;
		this.flights = flights;
	}
	
	/**
	 * Returns the client of Booking.
	 * 
	 * @return the client.
	 */
	public String getClient() {
		return client;
	}

	/**
	 * Sets client to a given string.
	 * 
	 * @param client the client to set.
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * Returns the flights of Booking.
	 * 
	 * @return the flights.
	 */
	public Itinerary getFlights() {
		return flights;
	}

	/**
	 * Sets flights to a given Itinerary.
	 * 
	 * @param flights the flights to set.
	 */
	public void setFlights(Itinerary flights) {
		this.flights = flights;
	}
}
