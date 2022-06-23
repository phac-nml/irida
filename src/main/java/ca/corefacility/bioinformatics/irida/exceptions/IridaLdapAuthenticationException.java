package ca.corefacility.bioinformatics.irida.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when an error occurs while trying to create a new user via LDAP/ADLDAP credentials
 */
public class IridaLdapAuthenticationException extends AuthenticationException {
	private int errorCode;

	/**
	 * Creates a new {@link IridaLdapAuthenticationException} with error code
	 * @param msg String
	 * @param errorCode int alighed with the LoginPage.ldap_error.description_X properties in messages.properties
	 */
	public IridaLdapAuthenticationException(String msg, int errorCode) {
		super(msg);
		this.errorCode = errorCode;
	}

	public IridaLdapAuthenticationException(String msg, Throwable cause, int errorCode) {
		super(msg, cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
