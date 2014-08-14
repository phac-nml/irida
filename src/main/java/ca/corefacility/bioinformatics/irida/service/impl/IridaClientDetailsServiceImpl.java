package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

/**
 * Service for storing and retrieving {@link IridaClientDetails} object.
 * Implements {@link ClientDetailsService} for use with OAuth approvals.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service("clientDetails")
public class IridaClientDetailsServiceImpl extends CRUDServiceImpl<Long, IridaClientDetails> implements
		IridaClientDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaClientDetailsServiceImpl.class);
	private IridaClientDetailsRepository clientDetailsRepository;
	private TokenStore tokenStore;

	@Autowired
	public IridaClientDetailsServiceImpl(IridaClientDetailsRepository repository, TokenStore tokenStore,
			Validator validator) {
		super(repository, validator, IridaClientDetails.class);
		this.clientDetailsRepository = repository;
		this.tokenStore = tokenStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		IridaClientDetails client = clientDetailsRepository.loadClientDetailsByClientId(clientId);
		if (client == null) {
			throw new NoSuchClientException("Client with this clientId does not exist: " + clientId);
		}
		return client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaClientDetails create(IridaClientDetails object) throws ConstraintViolationException,
			EntityExistsException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaClientDetails update(Long id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		return super.update(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Long id) throws EntityNotFoundException {
		IridaClientDetails read = read(id);
		String clientId = read.getClientId();
		logger.debug("Deleting client " + clientId);
		Collection<OAuth2AccessToken> findTokensByClientId = tokenStore.findTokensByClientId(clientId);
		logger.debug("Removing " + findTokensByClientId.size() + " tokens for client");
		for (OAuth2AccessToken token : findTokensByClientId) {
			tokenStore.removeAccessToken(token);
		}

		super.delete(id);
	}

}
