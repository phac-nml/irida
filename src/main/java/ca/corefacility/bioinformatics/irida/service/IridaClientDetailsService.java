package ca.corefacility.bioinformatics.irida.service;

import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Service for storing and reading {@link IridaClientDetails} objects
 * 
 *
 */
public interface IridaClientDetailsService extends ClientDetailsService, CRUDService<Long, IridaClientDetails> {

	@PreAuthorize("permitAll()")
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public IridaClientDetails create(IridaClientDetails entity);

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Override
	public IridaClientDetails update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) throws EntityNotFoundException;

}
