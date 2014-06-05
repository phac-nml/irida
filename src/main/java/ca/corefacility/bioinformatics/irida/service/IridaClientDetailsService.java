package ca.corefacility.bioinformatics.irida.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Service for storing and reading {@link IridaClientDetails} objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaClientDetailsService extends ClientDetailsService, CRUDService<Long, IridaClientDetails> {

	@PreAuthorize("permitAll()")
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;
	/**
	 * Add a new {@link IridaClientDetails} object to the database
	 * @param entity the new object to add
	 * @return the persisted entity
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public IridaClientDetails create(IridaClientDetails entity);
	
	/**
	 * Delete an {@link IridaClientDetails} object from the database
	 * @param id The ID to delete
	 * @throws EntityNotFoundException if the entity does not exist
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) throws EntityNotFoundException;
	

}
