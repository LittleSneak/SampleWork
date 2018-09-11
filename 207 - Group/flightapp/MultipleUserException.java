package flightapp;

/**
 * Created by alexfalconer-athanassakos on 2015-11-28.
 */
public class MultipleUserException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6688529018796810041L;

	public MultipleUserException(String message){
        super(message);
    }
}
