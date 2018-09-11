/**
 * 
 */
package flightapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.Set;

import graph.DirectionalGraph;
import graph.NoSuchNodeException;
import graph.Node;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.text.ParseException;

/** A class with the back-end methods that will allow
 *  any front-end classes to interact with the classes
 *  designed, or, at minimum, for Flight and User data
 *  to be input from CSV files, and output in the form
 *  of serialized files.
 *  
 */
public class FlightApp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7838557344220967430L;

	/* Instance Variables. */
	//Graph to link flight paths for creating itineraries.
	private DirectionalGraph<Flight> graph;
	
	/* Files for persistent storage. */
	private File userStorage; //File with serialized User objects.
	private File flightStorage;  //File with serialized Flight objects.
	/* Hash maps for storing Users and Flights. */
	private HashMap<String, Client> userHashMap;
	//Map of usernames and Users.
	private HashMap<Integer, Flight> flightHashMap;
	//String for storing path to password.txt file.
	private String passwordStoragePath = null;
	
	/** Initialises a new FlightApp.
	 */

	public FlightApp() {
		// Initialise the graph for storing Flights
		this.graph = new DirectionalGraph<Flight>();
		/* initialise the hash maps. */
		this.userHashMap = new HashMap<String, Client>();
		this.flightHashMap  = new HashMap<Integer, Flight>();
	}

	/** Initialises a new FlightApp.
	 * 
	 * @param userStoragePath path to serialised user data.
	 * @param flightStoragePath path to serialised flight data.
	 * @throws FileNotFoundException if one or both of the files do not exist.
	 * @throws IOException if the files cannot be read.
	 */

	public FlightApp(String userStoragePath, String flightStoragePath) {
		// Initialise the graph for storing Flights
		this.graph = new DirectionalGraph<Flight>();
		/* initialise the hash maps before deserialisation. userHashMap is set
		 * to null because the hash map from the file is simply stored into
		 * the variable. With flighthashMap, operations are a bit more complex
		 * so it needs to be completely initialised. */
		this.userHashMap = null;
		this.flightHashMap  = new HashMap<Integer, Flight>();
		/* Initialize the File objects with the paths passed
		 * as parameters if files are extant at the paths given. */
		this.userStorage = new File(userStoragePath);
		this.flightStorage = new File(flightStoragePath);
		/* Reads persistent data from disk. */
		this.readPersistentData();
	}
	
	/** Initialises a new FlightApp.
	 * 
	 * @param userStoragePath path to serialised user data.
	 * @param flightStoragePath path to serialised flight data.
     * @param passwordStoragePath path to CSV format file with
     *                          user names and passwords.
	 * @throws FileNotFoundException if one or both of the files do not exist.
	 * @throws IOException if the files cannot be read.
	 */

	public FlightApp(String userStoragePath, String flightStoragePath,
                     String passwordStoragePath) {
		// Initialise the graph for storing Flights
		this.graph = new DirectionalGraph<Flight>();
		/* initialise the hash maps before deserialisation. userHashMap is set
		 * to null because the hash map from the file is simply stored into
		 * the variable. With flighthashMap, operations are a bit more complex
		 * so it needs to be completely initialised. */
		this.userHashMap = null;
		this.flightHashMap  = new HashMap<Integer, Flight>();

		/* Initialize the File objects with the paths passed
		 * as parameters if files are extant at the paths given. */
		this.userStorage = new File(userStoragePath);
		this.flightStorage = new File(flightStoragePath);
		this.passwordStoragePath = passwordStoragePath;

		/* Reads persistent data from disk. */
		this.readPersistentData();
	}

	/** Reads the Flight and User information from the files on disk.
	 * 
	 * @throws IOException If the files to the User and Flight data
	 *  cannot be opened or read.
	 * @throws ClassNotFoundException If a serialised class of that
	 *  type cannot be found in the file 
	 */
	@SuppressWarnings("unchecked")
	public void readPersistentData() {
		//create a temporary map to store the flight information.
		HashMap<Integer, Flight> fHashMap = null;
		try {
			/* Open file if it exists. */
			FileInputStream fisFlightStorage = 
					new FileInputStream(this.flightStorage);
			// Create an object input stream for deserialising the information.
			ObjectInputStream oisFlightStorage =
					new ObjectInputStream(fisFlightStorage);
			/* Read the hash map from the file. */
			fHashMap  =
					(HashMap<Integer, Flight>) oisFlightStorage.readObject();
			/* Close object stream. */
			oisFlightStorage.close();
			/* Close file stream. */
			fisFlightStorage.close();
		}
		catch(IOException e){
		}
		catch(ClassNotFoundException c){
		}
		catch(NullPointerException e){
		}
		try {
			/* Open the file if it exists. */
			FileInputStream fisUserStorage = 
					new FileInputStream(this.userStorage);
			// Create an object input stream for deserialising the information.
			ObjectInputStream oisUserStorage = 
					new ObjectInputStream(fisUserStorage);
			/* Read the hash map from the file. */
			this.userHashMap =
					(HashMap<String, Client>) oisUserStorage.readObject();
			/* Close object stream. */		
			oisUserStorage.close();
			/* Close file stream. */
			fisUserStorage.close();
		}
		catch(IOException e){
            e.printStackTrace();
		}
		catch(ClassNotFoundException c){
            c.printStackTrace();
		}
		catch(NullPointerException e){
            e.printStackTrace();
		}
		/* If nothing was retrieved, create a new hash map. */
		if ( this.userHashMap == null ) {
			this.userHashMap = new HashMap<String, Client >();
		}
		if ( !( fHashMap  == null) )  {
		/* Add the flights in the hash map to the system.  */
			this.addFlight(fHashMap);
		}
		if (this.passwordStoragePath != null )
			this.addUsersFromCSV(this.passwordStoragePath);
    }
	
	/** Gets users from the supplied CSV-format text file.
	 * 
	 * @param path to password file.
	 */
	public void addUsersFromCSV(String path) {
		try {
			File passwordStorage; //CSV format file with
			// user names and passwords.
			passwordStorage = new File(path);
			List<String[]> usernamePasswords; /* list to store the
			 * deserialised information. */
			//Deserialise the CSV file.
			usernamePasswords = this.readFromCSVFile(passwordStorage);
			for (String[] data: usernamePasswords) {
				//check if the email already exists.
				if ( this.userHashMap.containsKey(data[0]) ) {
					//set the username and password if so.
					this.userHashMap.get(data[0]).setUsername(data[0]);
					this.userHashMap.get(data[0]).setPassword(data[1]);
				}
				else {
					if ( data[2].equals("client") ) {
						//create a new client.
						Client user = new Client(data[0],data[1]);
						user.setEmail(data[0]);
						//add the user to the hash map.
						this.userHashMap.put(data[0],user);
					}
					else if ( data[2].equals("admin") ) { //make a new admin.
						Admin user = new Admin(data[0],data[1]);
						user.setEmail(data[0]);
						//add the user to the hash map.
						this.userHashMap.put(data[0],user);
					}
				}
			}

		} catch (FileNotFoundException e) {
            System.out.println("The file " + path + "was not found.");
            e.printStackTrace();
		}
	}

	/** Writes the Flight and User information to the files on disk.
	 * 
	 * @throws IOException If an error is encountered while opening the file
	 * or saving the objects to the file.
	 */
	public void savePersistentData(){
		//Try running the code.
		try{
			/* Open file if it exists, create it if not. */
			FileOutputStream fisUserStorage = 
					new FileOutputStream(this.userStorage);
			// Create an object output stream for serialising the information.
			ObjectOutputStream oosUserStorage = 
					new ObjectOutputStream(fisUserStorage);
			/* Write the hash map to the file. */
			oosUserStorage.writeObject(this.userHashMap);
			/* Close object stream. */
			oosUserStorage.close();
			/* Close file stream. */
            fisUserStorage.close();
		}
		catch(IOException e){ //Catch an exception if it occurs.
		}
		try{
			/* Open file if it exists, create it if not */
			FileOutputStream fisFlightStorage = 
					new FileOutputStream(this.flightStorage);
			// Create an object output stream for serialising the information.
			ObjectOutputStream oosFlightStorage = 
					new ObjectOutputStream(fisFlightStorage);
			/* Write the hash map to the file. */
			oosFlightStorage.writeObject(this.flightHashMap);
			/* Close object stream. */
			oosFlightStorage.close();
			/* Close file stream. */
			fisFlightStorage.close();
		}
		catch(IOException e){ //Catch an exception if it occurs.
		}
	}

    /**
     * Uploads the user information from the file and updates the user
     * information stored in the application.
     *
     * @param path path to the file with the user billing and personal
     *             information.
     */
    public void uploadUserInfo(String path) {
        //Get the file.
        File infoFile = new File(path);
        //Create a list for storing the information.
        List<String[]> userInfoList = new ArrayList<String[]>();
        try { //Read the information from the file.
            userInfoList.addAll(this.readFromCSVFile(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Loop through each of the String arrays.
        for (String[] data: userInfoList ) { //The format is as follows:
            //LastName,FirstNames,Email,Address,CreditCardNumber,ExpiryDate.
            //Get the user with the e-mail address given.
            Client user = this.userHashMap.get(data[2]);
            //Update the user information.
            user.setFirstName(data[1]);
            user.setUsername(data[2]);
            user.setLastName(data[0]);
            user.setAddress(data[3]);
            user.setCreditCardNumber(data[4]);
            user.setExpirationDate(data[5]);
        }
    }

	/**
	 * Adds a new Flight to the FlightApp
	 * 
	 * @param flightNumber An integer which represents the number
	 *  for the flight. Each flight has a unique number.
	 * 
	 * @param departure A ZonedDateTime object which represents the time
	 * and date that the flight takes off.
	 * 
	 * @param arrival A ZonedDateTime object which represents the time
	 * and date that the flight arrives at its destination.
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
	 *  @param price a double which represents the cost of the flight
	 *
     *  @param numSeats an int which represents the number of available seats.
	 */
	public void addFlight(int flightNumber, String departure, 
			String arrival, String airline, String origin,
			String destination, double price, int numSeats) {
		//Create a new Flight with the information from the parameters passed.
		Flight flight = new Flight(flightNumber, departure, arrival,
				airline, origin, destination, price, numSeats);

		this.addFlight(flight);
	}
	
	/*
	 * Adds a Flight to this FlightApp's graph, and to persistent storage.
	 */
	/** Adds a Flight to the FlightApp.
	 * 
	 * @param flight the Flight to add to the FlightApp.
	 */
	public void addFlight(Flight flight) {
		//Throw an exception if a flight with the same flight number exists.
		Integer flightNum = new Integer(flight.getFlightNumber());

		//Add the Flight to the hash map.
		this.flightHashMap.put(flightNum, flight);

		//Add the flight to the graph.
		try {
			this._flightToGraph(flight);
		} catch (NoSuchNodeException e) {
			e.printStackTrace();
		}
	}

	/** Adds Flights from the given flightMap to the FlightApp.
	 * 
	 * @param flightMap the hash map from which to add
	 * flights to the FlightApp. It is presupposed that none of
	 * the flights in the hash map have the same keys as the ones
	 * in the FlightApp's flightHashMap, or if so, updating of the
	 * values (flights) with conflicting keys is the intended effect.
	 */
	public void addFlight(HashMap<Integer, Flight> flightMap) {
		
		/* Add the flights from the hash maps to the FlightApp's
		 * hash maps and graph. */
		this.flightHashMap.putAll(flightMap);

		for ( Flight flight: flightMap.values() ) {
			//Add the flight to the graph.
			try {
				this._flightToGraph(flight);
			} catch (NoSuchNodeException e) {
			}
		}
	}
	
	/** Adds flights from the file at path given to the FlightApp.
	 *  Updates existing information if flights with the same flight
	 *  numbers exist.
	 * 
	 * @param path to file with Flight information
	 * @throws FileNotFoundException if no file exists at path
	 * @throws ParseException if the file information cannot be parsed
	 */
	public void addFlight(String path) throws FileNotFoundException,
	ParseException {
		//Check if the file exists.
		if ( !new File(path).exists() ) {
			/* Throw an Exception if file at path is not found. */
			throw new FileNotFoundException("No file found at: " + path);
		}
		//Loop through the deserialised entries from the file.
        File flightsFile = new File(path); //get the file.

		for (String[] data: this.readFromCSVFile(flightsFile)){

			//Add flight
			int flightNum = Integer.parseInt(data[0]);
			int numSeats = Integer.parseInt(data[7]);

			this.addFlight(flightNum, data[1], data[2], data[3],
					data[4], data[5],
					Double.parseDouble((data[6])),numSeats);
		}
	}
	
	/** Adds a Flight to this FlightApp's graph structure. 
	 * 	Adds an edge to Node with value flight where:
	 * 	- prior Node's flight has same destination as flight's departure.
	 *  - prior Node's flight has arrival time that is at or before flight's
	 *    departure.
	 *  - prior Node's flight's arrival time is no greater than 6 hours before
	 *    flight's departure. 
	 * @param flight flight to be added to this Flight's graph.
	 * @throws NoSuchNodeException If an inconsistency between connecting
	 * Flights in this FlightApp and those expected in this FlightApp's graph
	 * arises.
	 */
	public void _flightToGraph(Flight flight) throws NoSuchNodeException {
		
		this.graph.addNode(flight.getFlightNumber(), flight);
		
		/* Find all Flight with matching criteria from this FlightApp's
		 * hash map */
		Set<Flight> matchesTo = new HashSet<Flight>();
		Set<Flight> matchesFrom = new HashSet<Flight>();
		
		for (Flight f: this.flightHashMap.values()){
			//Time from f arrival to flight departure no less than 0 hours.
			if ((flight.getDeparture().getTimeInMillis()
					- f.getArrival().getTimeInMillis())/3.6e6 >= 0){
				if (f.getDestination().equals(flight.getOrigin())
						//Time from flight departure to
						&& (( (
						flight.getDeparture().getTimeInMillis()
						- f.getArrival().getTimeInMillis()
						) / 60000 ) <= 360 )) {
					matchesFrom.add(f);
				}
				//Time from flight arrival to f departure >= 0 minutes
			} else if (
					((f.getDeparture().getTimeInMillis()
							- flight.getArrival().getTimeInMillis())
							/ 60000) >= 0 ){
				if (flight.getDestination().equals(f.getOrigin())
						//Time from flight arrival to f departure<=360 minutes.
						&& (((f.getDeparture().getTimeInMillis()
						- flight.getArrival().getTimeInMillis())/
                        60000) <= 360)){
					matchesTo.add(f);
				}
			}
		}		
		
		for (Flight match: matchesTo){
			try {
				this.graph.addEdge(this.graph.getNode(flight),
						this.graph.getNode(match));
			}
			catch (NoSuchNodeException n){
			}

		}
		
		for (Flight match: matchesFrom){
			try {
				this.graph.addEdge(this.graph.getNode(match),
						this.graph.getNode(flight));
			}
			catch(NoSuchNodeException n){
			}
		}

	}
	
	/** Adds User with the information provided.
	 * 
	 * @param password A string which is the password of the client.
	 * @param firstName A string that represents the first name.
	 * @param lastName A string that represents the last name.
	 * @param address A string that represents the address.
	 * @param email A string that represents the email.
	 * @param creditCardNumber A string which is the credit card
	 * number of the user.
	 * @param expirationDate A string which is the expiration date of the
	 * user's credit card.
	 */
	public void addUser(String password, String lastName
			, String firstName, String email, String address
			, String creditCardNumber, String expirationDate) {

		//Create a new user
		Client user = new Client(email, password, lastName
				, firstName, email, address
				, creditCardNumber, expirationDate);

		//Add user to email-User hash map.
		this.userHashMap.put(email, user);

	}

	/** Adds User(s) from the file at path to the userList
	 *
	 * @param path to file with User information
	 * @throws FileNotFoundException if no file exists at path
	 */
	public void addUser(String path) throws FileNotFoundException {

		if  (!new File(path).exists() ) {

			/* Throw an Exception if file at path is not found. */
			throw new FileNotFoundException("No file found at: " + path );
		}
		else {

            //get the file.
            File userInfoFile = new File(path);

            //Read the information from the csv file passed.
			for (String[] data: readFromCSVFile(userInfoFile)){

				//add user to email-User hash map.
				this.addUser("", data[0], data[1], data[2],
                        data[3], data[4], data[5]);
			}
		}
	}
	
	/** Returns true iff the dates of first and second are equal,
	 * false otherwise.
	 * 
	 * @param first date to compare to
	 * @param second date to compare with first
	 * @return true iff the dates of first and second are equal.
	 */
	public static boolean compareDates(GregorianCalendar first,
								 GregorianCalendar second) {
		/* Compare the day, month, and year fields of the dates. */

    	if ( first.get(GregorianCalendar.DAY_OF_MONTH)
                == second.get(GregorianCalendar.DAY_OF_MONTH)
    			& first.get(GregorianCalendar.MONTH)
                == second.get(GregorianCalendar.MONTH)
    			& first.get(GregorianCalendar.YEAR)
                == second.get(GregorianCalendar.YEAR) ) {
    		return true; //return true if they are equivalent.
    	}
    	return false; //return false otherwise.
	}
	
	/** Returns a set of Itineraries matching the departureDate,
	 *  origin and destination given.
	 * 
	 * @param departureDate Flight departure date for use in itinerary search
	 * @param origin Flight origin for use in itinerary search
	 * @param destination Flight destination for use in itinerary search
	 * @return Set of Itinerary
	 */
	public Set<Itinerary> searchItineraries
	(GregorianCalendar departureDate, String origin, String destination ) {
		//Create a list to store the found paths, there may be duplicates.
		List<List<Node<Flight>>> pathList = new ArrayList<>();
		//Create a list to store all flights from origin on the departureDate.
		List<Flight> originList = new ArrayList<Flight>();
		//Create a list to store all flights arriving at destination.
		List<Flight> destinationList = new ArrayList<Flight>();
		/* Create a set to store itineraries found. Use of a HashSet will allow
		 * duplicates to be removed automatically when adding elements to it.*/
		HashSet<Itinerary> itinerarySet = new HashSet<Itinerary>();
		/* Loop through all flights in the flight hash map. */
		
		for (Flight flight : this.flightHashMap.values()) {
			if (flight.getOrigin().equals(origin) ) {
				//check if the departure dates are equivalent.
				if ( FlightApp.compareDates(departureDate, flight.getDeparture()) ){
					originList.add(flight);
				}
			} //check if flight will be arriving at destination
			if (flight.getDestination().equals(destination) ) {
				destinationList.add(flight);
			}
		} /* End loop. */
		
		/* Get the possible routes (paths) and store to pathList.*/
		//loop through the possible origin flights.
		for (Flight originFlight: originList) {
			//loop through the possible destination flights.
			for (Flight destFlight: destinationList) {
				/* check if the flight originates from origin and gets to
				 * the destination, such that it's the only one necessary
				 * to fulfill the itinerary. */
				if (originFlight.getFlightNumber() ==
				destFlight.getFlightNumber()) {
					List<Node<Flight>> path = new ArrayList<Node<Flight>>();
					//add the new node to the path.
					path.add(new Node<Flight>
					(originFlight.getFlightNumber(), originFlight));
					pathList.add(path);
					continue; //go to the next iteration of the inner loop.
				}
				try { 	/* Get the Nodes of the flights in the Graph. */
					Node<Flight> originNode = 
							this.graph.getNode(originFlight.getFlightNumber());
					Node<Flight> destNode = 
							this.graph.getNode(destFlight.getFlightNumber());
					//Get the possible paths.
					pathList.addAll(this.graph.getPaths(originNode, destNode));
				} catch (NoSuchNodeException e) {
				}
			} /* End for loop. */
		}
		addToItin: //label the loop.
			for ( List<Node<Flight>> nodeList: pathList ) {
				//Convert the list of nodes to a list of flights.
				List<Flight> flightPath = graph.unboxNodes(nodeList);
				for (Flight flight: flightPath) {
					//Skip adding the itinerary since there's no more seats
					//in one of the required flights.
					if (flight.getNumSeats() == 0 ) { continue addToItin; }
				}
				//Create an itinerary from the flightPath.
				Itinerary itinerary = new Itinerary(flightPath);
				//Add the  itinerary to the set.
				itinerarySet.add(itinerary);
			}
		return itinerarySet; //return the set of itineraries.
	}
	
	/** Returns a set of Itineraries matching the date,
	 *  origin and destination given.
	 * 
	 * @param date Flight departure date for use in itinerary search.
	 * @param origin Flight origin for use in itinerary search.
	 * @param destination Flight destination for use in itinerary search.
	 * 
	 * @return Set of Itinerary.
	 */
	public Set<Itinerary> searchItineraries(String date, String origin, 
			String destination){
		/* Convert the string 'date' entered into a format we can use. */
		GregorianCalendar dep = Flight._parseDateString(date);
		/* pass the new date dep, and the origin and destination to the
		 * method's first overload version. */
		return searchItineraries(dep, origin, destination);
	}
	
	/** Returns a Booking  for user created from an itinerary.
	 * 
	 * @param user Client for whom booking is made.
	 * @param itinerary the itinerary to add to booking.
	 * @return Booking.
	 */
	public Booking createBooking(Client user, Itinerary itinerary) {
		//Create a new booking.
		Booking booking = new Booking(user.getUsername(), itinerary);
		return booking;
	}
	
	/** Returns a List of  Bookings for user
	 *  created from a list of itinerary given.
	 * 
	 * @param user Client for whom booking is made
	 * @param itineraries the itinerary to add to booking
	 * @return Booking
	 */
	public List<Booking> createBooking
	(Client user, List<Itinerary> itineraries) {
		//create a new list to store bookings.
		List<Booking> bookings = new ArrayList<Booking>();
		for (Itinerary itinerary: itineraries) {
			//Create a new booking.
			Booking booking = new Booking(user.getUsername(), itinerary);
			bookings.add(booking); //add the booking to the list.
		}
		return bookings; //return the list.
	}
	
	/** Returns a Booking  for user with username created from an itinerary.
	 * 
	 * @param username Client with username for whom booking is made
	 * @param itinerary the itinerary to add to booking
	 * @return Booking
	 */
	public Booking createBooking
	(String username, Itinerary itinerary) {
		//Create a new booking.
		Booking booking = new Booking(username, itinerary);
		return booking; 
		}
	
	/** Returns a List of  Bookings for user with username
	 *  created from a list of itinerary given.
	 * 
	 * @param username Client with username for whom booking is made.
	 * @param itineraries the itinerary to add to booking.
	 * @return Booking.
	 */
	public List<Booking> createBooking
	(String username, List<Itinerary> itineraries) {
		//create a new list to store bookings.
		List<Booking> bookings = new ArrayList<Booking>();
		for (Itinerary itinerary: itineraries) {
			//Create a new booking.
			Booking booking = new Booking(username, itinerary);
			bookings.add(booking); //add the booking to the list.
		}
		return bookings; //return the list.
	} 
	
	/** Returns a List of array of String with deserialised csv information
	 * from the file at path given.
	 * 
	 * @param file to file with csv information.
	 * @return List of array of String.
	 * @throws FileNotFoundException if no file exists at path.
	 */
	public List<String[]> readFromCSVFile(File file)
            throws FileNotFoundException {

         //Initialise the file scanner.
        Scanner scanner = new Scanner(new FileInputStream(file));

        //Initialise a list to store the deserialised information.
        List<String[]> objects = new ArrayList<String[]>();
        //Go through all the lines in the file.
        while(scanner.hasNextLine()) {
        	//Split a line into substrings.
        	String[] info = scanner.nextLine().split(",");
        	//add the String array to the List.
            objects.add(info);

        }
        scanner.close(); //close the scanner.

        return objects; //return the list.
	}
	
	/** Returns a List of Flight sorted by cost.
	 * 
	 * @param flights Collection of flights to sort.
	 * @return List of Flight.
	 */
	public static List<Flight> sortFlightsByCost
	(Collection<Flight> flights){
		//create a list for storing the elements.
		List<Flight> items = new ArrayList<>(flights);
		//initialise the comparator.
		Comparator<Flight> comparator = new Comparator<Flight>() {
		    public int compare(Flight f1, Flight f2) {
		        return (int) (Math.floor(f1.getPrice()) -
		        		Math.floor(f2.getPrice()));
		    }
		};
		 //Sort the elements.
		Collections.sort(items, comparator);
		//return the sorted list.
		return items;
	}

	/** Returns a List of Itineraries sorted by cost.
	 * 
	 * @param itineraries Collection of itineraries to sort.
	 * @return List of Itinerary.
	 */
	
	public static List<Itinerary> sortItinerariesByCost
	(Collection<Itinerary> itineraries){
		//create a list for storing the elements.
		List<Itinerary> items = new ArrayList<>(itineraries);
		//initialise the comparator.
		Comparator<Itinerary> comparator = new Comparator<Itinerary>() {
		    public int compare(Itinerary i1, Itinerary i2) {
		        return (int) (Math.floor(i1.getTotalPrice()) -
		        		Math.floor(i2.getTotalPrice()));
		    }
		};
		 //Sort the elements.
		Collections.sort(items, comparator);
		//return the sorted list.
		return items;
	}

	/** Returns a set of Itineraries sorted by total travel time.
	 * 
	 * @param itineraries collection of itineraries to sort.
	 * @return List of Itinerary.
	 */
	public static List<Itinerary> sortItinerariesByTime
	(Collection<Itinerary> itineraries){
		//create a list for storing the elements.
		List<Itinerary> items = new ArrayList<>(itineraries);
		Collections.sort(items); //Sort the elements.
		//return the sorted list.
		return items;
	}
	
	/** Returns a set of Flight sorted by total travel time.
	 * 
	 * @param flights collection of flights to sort.
	 * @return List of Flight.
	 */
	public static List<Flight> sortFlightsByTime(Collection<Flight> flights){
		//create a list for storing the elements.
		List<Flight> items = new ArrayList<>(flights);
		Collections.sort(items); //Sort the elements.
		//return the sorted list.
		return items;
	}
	
	
	/** Returns the list of Users.
	 * @return a list of User.
	 */
	public List<User> getUserList() {
		return new ArrayList<User>(this.userHashMap.values());
	}

	/** Returns the list of Clients.
	 * @return a list of Client.
	 */
	public List<Client> getClientList() {
		return new ArrayList<Client>(this.userHashMap.values());
	}
	
	/** Returns the list of Flights.
	 * @return a list of Flight.
	 */
	public List<Flight> getFlightList() {
		return new ArrayList<Flight>(this.flightHashMap.values());
	}

	/**
	 * Returns all Flights with matching date, origin and departure
	 * @param 	date  	the String date of the flight (in YYYY-MM-DD)
	 * @param	origin		the String origin of the flight
	 * @param	destination	the String departure of the flight
	 * @return	A list of all flights with listed parameters
	 */
	public List<Flight> searchFlights(String date, String origin, String destination){
		List<Flight> flights = new ArrayList<Flight>();

		for (Flight f: this.getFlightList()){
			if (f.getDepartureString().substring(0, 10).equals(date)
					&& f.getOrigin().equalsIgnoreCase(origin)
					&& f.getDestination().equalsIgnoreCase(destination)){
				flights.add(f);
			}
		}

		return flights;
	}

	/**
	 * Returns the userHashMap
	 *
	 * @return A hashmap of string to client.
	 */
	public HashMap<String, Client> getUserHashMap(){

		return this.userHashMap;
	}

	/**
	 * Sets the user hashmap to a given hashmap of string to client
	 *
	 * @param newUserHashMap A hashmap of string to client
	 */
	public void setUserHashMap(HashMap<String, Client> newUserHashMap) {
		this.userHashMap = newUserHashMap;
	}

	/** Returns true iff the username and password match that of a
	 *  user in the userHashMap.
	 * @param username of a User.
	 * @param password of a User.
	 * @return true iff the username and password are valid details of a user.
	 * 
	 */
	public boolean isUserValid(String username, String password) {
		boolean result = false;
		//Disallow blank passwords.
		if (password.equals("")) return result;
		//check if the user is in the hash map.
		if ( this.userHashMap.containsKey(username)  ) {
			if (this.userHashMap.get(username).getPassword().equals(password))
				result = true;
			
		}
		return result;
	}

	/**
	 * Returns the flightHashMap
	 *
	 * @return A hashmap of Integer to Flight.
	 */
	public HashMap<Integer, Flight> getFlightHashMap(){

		return this.flightHashMap;
	}

	/**
	 * Sets the flight hashmap to a given hashmap of Integer to Flight
	 *
	 * @param newFlightHashMap A hashmap of Integer to Flight
	 */
	public void setFlightHashMap(HashMap<Integer, Flight> newFlightHashMap) {
		this.flightHashMap = newFlightHashMap;
	}
}
