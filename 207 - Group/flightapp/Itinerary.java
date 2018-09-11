package flightapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class represents an itinerary of flights.
 *
 */
public class Itinerary implements Serializable, Comparable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -4125595674149434828L;
	private List<Flight> flights;
    private String destination;
    private String departure;
    private String arrival;
    
    /**
     * Initalises an instance of Itinerary given a Flight.
     * 
     * @param f which is a Flight.
     */
    public Itinerary(Flight f){
    	this.flights = new ArrayList<Flight>();
    	this.flights.add(f);
    	this.destination = f.getDestination();
    	this.departure = f.getDepartureString();
    	this.arrival = f.getArrivalString();
    }
    
    /**
     * Initializes an instance of Itinerary given an
     * ArrayList of flights.
     * 
     * @param f which is an ArrayList of flights.
     */
    public Itinerary(List<Flight> f){
    	this.flights = f;
    	this.destination = f.get(f.size() - 1).getDestination();
    	this.departure = f.get(0).getDepartureString();
    	this.arrival = f.get(f.size() - 1).getArrivalString();
    }
    
    /** Returns the flights in the itinerary.
	 * @return the flights.
	 */
	public List<Flight> getFlights() {
		return flights;
	}

	/** Sets the flights in the Itinerary to the
	 * ones in the list given.
	 * 
	 * @param f the flights to set.
	 */
	public void setFlights(List<Flight> f) {
		this.flights = f;
		this.destination = f.get(f.size() - 1).getDestination();
    	this.departure = f.get(0).getDepartureString();
    	this.arrival = f.get(f.size() - 1).getArrivalString();
	}

	/** Returns the destination of the itinerary.
	 * 
	 * @return the destination.
	 */
	public String getDestination() {
		return destination;
	}

	/** Sets the destination in the Itinerary to the
	 *  one passed.
	 *  
	 * @param destination the destination to set.
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/** Returns the departure date.
	 * @return the departure.
	 */
	public GregorianCalendar getDeparture() {
		return this.flights.get(0).getDeparture();
	}

	/** Sets the departure to the one passed.
	 * @param departure the departure to set.
	 */
	public void setDeparture(String departure) {
		this.departure = departure;
	}

	/** Returns the arrival date of the Itinerary.
	 * 
	 * @return the arrival.
	 */
	public GregorianCalendar getArrival() {
		return this.flights.get(this.flights.size() - 1).getArrival();
	}

	/** Sets the Arrival date of the Itinerary.
	 * 
	 * @param arrival the arrival to set.
	 */
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	/**
	 * Returns a long which represents the total travel time in minutes of
	 * the Itinerary.
	 * 
	 * @return a long which is the total travel time between
	 * departure and arrival.
	 */
	public Integer calculateTravelTime(){
		return Integer.valueOf ( (int) 
				(Math.floor((this.getArrival().getTimeInMillis() -
                this.getDeparture().getTimeInMillis())/60000)));
	}
	
	/**
	 * Returns an integer which represents the total price of
	 * this itinerary trip.
	 * 
	 * @return an double which is the total price of this itinerary.
	 */
	public double getTotalPrice(){
		double price = 0; 
		for (int x = 0; x < this.flights.size(); x++){
			price = price + this.flights.get(x).getPrice();
		}
		return price;
	}
	
	/**
	 * Adds a given Flight to Itinerary. The flight must come after the
	 * last flight in Itinerary and the destination of the last flight
	 * in Itinerary must match the origin of the given flight.
	 * 
	 * @param f which is the flight that will be added to Itinerary.
	 */
	public void addFlight(Flight f){
			this.flights.add(f);
			this.destination = f.getDestination();
	    	this.departure = this.flights.get(0).getDepartureString();
	    	this.arrival = f.getArrivalString();
	}
	
	/**
	 * Return a string representation of Itinerary.
	 * 
	 * @return A string which represents Itinerary.
	 */
	public String toString(){
		String retStr = "";
		for(Flight flight: flights){
			String f = flight.toString();
			retStr += f.substring(0, f.lastIndexOf(',')) + "\n";
		}
		retStr += String.format("%.2f", this.getTotalPrice()) + "\n";
		
		long totalTime = this.calculateTravelTime();
		
		long hours = totalTime / 60 ;
		long mins = totalTime % 60;

		retStr += String.format("%02d", hours ) + ":" +
                String.format("%02d",mins);
		
		return retStr;
	}

	/**
	 * Returns true if the given Itinerary is equal to this.
	 *
	 * @param flights The Itinerary that this is being compared to.
	 * @return A boolean which is true if this is equal to the given
	 *         Itinerary and false otherwise.
	 */
	public boolean equals(Itinerary flights){
        return (this.destination.equals(flights.destination) &&
				this.departure.equals(flights.departure)
				&& this.arrival.equals(flights.arrival));
	}
	
    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted
     *                 into something comparable to {@code this} instance.
     */
	@Override
	public int compareTo(Object another) {
		Itinerary itinerary = (Itinerary) another;
        return this.calculateTravelTime().intValue() -
        		itinerary.calculateTravelTime().intValue();
	}
}
