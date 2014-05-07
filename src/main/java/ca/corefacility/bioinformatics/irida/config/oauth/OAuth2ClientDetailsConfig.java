package ca.corefacility.bioinformatics.irida.config.oauth;

import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Interface for configuration classes describing the storage of OAuth2 Client Details
 * @author "Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>"
 *
 */
public interface OAuth2ClientDetailsConfig {

	/**
	 * Return the ClientDetailsService to be used in the application 
	 * @return a {@link ClientDetailsService}
	 */
	public ClientDetailsService clientDetails();
}
