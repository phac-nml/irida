package ca.corefacility.bioinformatics.irida.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Service for storing and reading {@link IridaClientDetails} objects
 * 
 *
 */
public interface IridaClientDetailsService extends ClientDetailsService, CRUDService<Long, IridaClientDetails> {

	/**
	 * {@inheritDoc}
	 */
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

	/**
	 * Get the number of tokens issued for a given {@link IridaClientDetails}
	 * 
	 * @param client
	 *            Client to count tokens for
	 * @return Number of tokens issued for the given client
	 */
	public int countTokensForClient(IridaClientDetails client);

	/**
	 * Revoke all OAuth2 tokens for a given {@link IridaClientDetails}
	 * 
	 * @param client
	 *            The client to revoke tokens for
	 */
	public void revokeTokensForClient(IridaClientDetails client);

	/**
	 * Get the number of all tokens defined for a given
	 * {@link IridaClientDetails} that are valid and not expired.
	 * 
	 * @param client
	 *            the {@link IridaClientDetails} to get tokens for
	 * @return number of tokens defined for the client.
	 */
	public int countActiveTokensForClient(IridaClientDetails client);

	/**
	 * Search through {@link IridaClientDetails}
	 *
	 * @param specification
	 * 		The search {@link Specification}
	 * @param pageRequest
	 * 		The {@link PageRequest}
	 *
	 * @return {@link Page} of {@link IridaClientDetails} based on search requirements.
	 */
	public Page<IridaClientDetails> search(Specification<IridaClientDetails> specification, PageRequest pageRequest);
}
