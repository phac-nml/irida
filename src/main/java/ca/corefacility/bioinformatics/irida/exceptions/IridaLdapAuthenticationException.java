package ca.corefacility.bioinformatics.irida.exceptions;

import org.springframework.security.core.AuthenticationException;

public class IridaLdapAuthenticationException extends AuthenticationException {
	private int errorCode;

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