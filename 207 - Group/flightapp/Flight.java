/**
 * 
 */

package flightapp;
import java.util.GregorianCalendar;
import java.io.Serializable;

/**
 * A class that represents a flight for an airline company.
 * @author alexfalconer-athanassakos
 *
 */
public class Flight implements Serializable, Comparable {
	/** 
	 * serialVersionUID generated serial identifier.
	 */
	private static final long serialVersionUID = 3505375668355489116L;
	//"City, Country"
	private String origin;
	//"City, Country"
	private String destination;
	private GregorianCalendar departure;
	private GregorianCalendar arrival;
	private int flightNumber;
	private double price;
	private String airline;
	private int numSeats;
	

	/**
	 * Initializes an instance of Flight.
	 * 
	 * @param flightNumber An integer which represents the number for the 
	 * flight. 
	 * Each flight has a unique number.
	 * 
	 * @param departure A String which represents the departure date of the
	 * flight in the format yyyy-MM-dd HH:mm .
	 * 
	 * @param arrival A String which represents the arrival date of the
	 * flight in the format yyyy-MM-dd HH:mm .
	 * 
	 * @param airline A string which represents the name of the airline
	 * company for this specific flight.
	 * 
	 * @param origin A string which represents the location that the
	 * flight will begin at.
	 * 
	 * @param destination A string which represents the location that
	 * the flight will go to.
	 * 
	 *  @param price a double which represents the cost of the flight.
	 *
	 *  @param numSeats an int which represents the number of available seats.
	 */
	public Flight(int flightNumber, String departure, 
			String arrival, String airline, String origin, 
			String destination, double price, int numSeats) {
		this.flightNumber = flightNumber;
		this.departure = Flight._parseDateString(departure);
		this.arrival = Flight._parseDateString(arrival);
		this.airline = airline;
		this.origin = origin;
		this.destination = destination;
		this.price = price;
		this.numSeats = numSeats;
	}

	/**
	 * Parses a given string to a GregorianCalendar.
	 *
	 * @param string The date in the format yyyy-mm-dd.
	 * @return The GregorianCaldendar object.
	 */
	public static GregorianCalendar _parseDateString(String string){
		
		int year = Integer.parseInt(string.substring(0, 4));
		int month = Integer.parseInt(string.substring(5, 7));
		month--; //GregorianCalendar uses zero indexing for months.
		int day = Integer.parseInt(string.substring(8, 10));
		if (string.length() > 10) {
		int hour = Integer.parseInt(string.substring(11, 13));
		int minute = Integer.parseInt(string.substring(14, 16));
		return new GregorianCalendar(year, month, day, hour, minute);
		}
		return new GregorianCalendar(year, month, day);
	}

	/**
	 * Returns the origin of Flight.
	 * 
	 * @return the origin.
	 */
	public String getOrigin() {
		return origin;
	}


	/**
	 * Sets origin of this Flight to a given String.
	 * 
	 * @param origin the origin to set.
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}


	/**
	 * Returns the destination of Flight.
	 * 
	 * @return the destination.
	 */
	public String getDestination() {
		return destination;
	}


	/**
	 * Sets destination to a given string.
	 * 
	 * @param destination the destination to set.
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

    /** Returns a string representation of cal in the format
     *  YYYY-MM-DD HH:MM.
     *
     * @param cal Calendar to process.
     * @return the String representation of cal.
     */
    public static String calendarToString(GregorianCalendar cal) {
        //Format: YYYY-MM-DD HH:MM.
        String date = ""; //String to store the final date.
        String min = ""; //String for the minute.
        String hour = ""; //String for the hour.
        String mon = ""; //String for the month.
        String day = ""; //String for the day.
        int year       = cal.get(GregorianCalendar.YEAR);
        // GregorianCalendar.JANUARY = 0, not 1.
        int month      = cal.get(GregorianCalendar.MONTH);
        month++; //Normalise the number.
        int dayOfMonth = cal.get(GregorianCalendar.DAY_OF_MONTH);
        // 24 hour clock.
        int hourOfDay  = cal.get(GregorianCalendar.HOUR_OF_DAY);
        int minute     = cal.get(GregorianCalendar.MINUTE);
        //Pad the integers with zeroes.
        if ( minute < 10 ) { 
        	min = "0"+minute; 
        	}
        else { 
        	min = String.valueOf(minute); 
        	}
        if ( dayOfMonth < 10 ) { 
        	day = "0"+dayOfMonth; 
        	}
        else { 
        	day = String.valueOf(dayOfMonth); 
        	}
        if ( month < 10 ) { 
        	mon = "0"+month; 
        	}
        else { 
        	mon = String.valueOf(month); 
        	}
        if ( hourOfDay < 10 ) { 
        	hour = "0"+hourOfDay; 
        	}
        else { 
        	hour = String.valueOf(hourOfDay); 
        	}

        date = year + "-" + mon + "-" + day + " " + hour + ":" + min;
        return date;
    }

	/**
	 * Returns departure of Flight as a GregorianCalendar.
	 * 
	 * @return the departure.
	 */
	public GregorianCalendar getDeparture() {
		return this.departure;
	}
	
	/**
	 * Returns departure of flight as a string.
	 * 
	 * @return A string which is departure.
	 */
	public String getDepartureString(){
       return Flight.calendarToString(this.departure);
	}

	/** 
	 * Sets departure to a given String.
	 * 
	 * @param departure the departure to set.
	 */
	public void setDeparture(String departure) {
		this.departure = Flight._parseDateString(departure);
	}

	/** Sets this Flight's departure date/time to departure.
	 * @param departure GregorianCalendar departure date/time.
	 */
	public void setDeparture(GregorianCalendar departure){
		this.departure = departure;
	}


	/**
	 * Sets arrival to a given String.
	 * 
	 * @param arrival the arrival to set.
	 */
	public void setArrival(String arrival) {
		this.arrival = Flight._parseDateString(arrival);
	}

	/** Sets this Flight's arrival date/time to arrival.
	 * @param arrival GregorianCalendar arrival date/time.
	 */
	public void setArrival(GregorianCalendar arrival){
		this.arrival = arrival;
	}

	/**
	 * Returns the arrival of Flight.
	 *
	 * @return the arrival.
	 */
	public GregorianCalendar getArrival() {
		return this.arrival;
	}

    /**
     * Returns a string which is arrival.
     * 
     * @return A string which is arrival.
     */
	public String getArrivalString(){
		return Flight.calendarToString(this.arrival);
	}
	/**
	 * Returns flightNumber of Flight.
	 * 
	 * @return the flightNumber.
	 */
	public int getFlightNumber() {
		return flightNumber;
	}

	/**
	 * Sets flightNumber to a given integer.
	 * 
	 * @param flightNumber the flightNumber to set.
	 */
	public void setFlightNumber(int flightNumber) {
		this.flightNumber = flightNumber;
	}
	
	/**
	 * Returns the price of Flight.
	 * 
	 * @return the price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets price to a given double.
	 * 
	 * @param price the price to set.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Returns the number of seats available on Flight.
	 *
	 * @return the number of seats.
	 */
	public int getNumSeats() {
		return this.numSeats;
	}

	/**
	 * Sets numSeats to the given int.
	 *
	 * @param numSeats the int to set as the numSeats.
	 */
	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}

	/**
	 * Returns airline of Flight.
	 * 
	 * @return the airline.
	 */
	public String getAirline() {
		return airline;
	}

	/**
	 * Sets airline to a given string.
	 * 
	 * @param airline the airline to set.
	 */
	public void setAirline(String airline) {
		this.airline = airline;
	}

	/**
	 * Calculates and returns the travel time, in minutes, as a double.
	 * @return double representation of minutes of travel from
     * origin to destination,
	 */
	public int calculateTravelTime() {
		return (int) Math.floor((this.arrival.getTimeInMillis() -
                this.departure.getTimeInMillis())
				/ 60000 );
	}
	
	/**
	 * Returns a string representation of Flight.
	 * 
	 * @return A string which represents Flight.
	 */
	public String toString(){
		return "" + this.flightNumber + "," + this.getDepartureString() + ","
				+ this.getArrivalString()  + "," + this.airline 
				+ "," + this.origin + "," + this.destination + ","
				+ String.format("%.2f", this.price);
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
        Flight f = (Flight) another;
        return this.calculateTravelTime() - f.calculateTravelTime();
    }

}
