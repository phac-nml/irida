package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

/**
 * Error message response class to be serialzed to Json and returned from the {@link ControllerExceptionHandler}
 * @author "Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>"
 *
 */
public class ErrorResponse{
	private String message;
	public ErrorResponse(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
