package ca.corefacility.bioinformatics.irida.security.permissions;

/**
 * Thrown when the {@link IridaPermissionEvaluator} is asked to test a
 * permission that it doesn't know about.
 * 
 * 
 */
public class UndefinedPermissionException extends RuntimeException {

	private static final long serialVersionUID = -5643657584405285035L;

	public UndefinedPermissionException(String message) {
		super(message);
	}
}
