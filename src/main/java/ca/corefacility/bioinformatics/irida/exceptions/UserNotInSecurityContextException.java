package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown if the currently logged in user cannot be read from the application's security context
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class UserNotInSecurityContextException extends RuntimeException{

	private static final long serialVersionUID = -2553364488019528528L;
	
	/**
	 * Create a new exception with the given message
	 * @param message
	 */
	public UserNotInSecurityContextException(String message){
		super(message);
	}
	
	/**
	 * Create a new exception with the given message and cause
	 * @param message
	 * @param cause
	 */
	public UserNotInSecurityContextException(String message,Throwable cause){
		super(message,cause);
	}

}
