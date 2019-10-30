package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

/**
 * Service for storing and retrieving {@link IridaClientDetails} object.
 * Implements {@link ClientDetailsService} for use with OAuth approvals.
 * 
 *
 */
@Service("clientDetails")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class IridaClientDetailsServiceImpl extends CRUDServiceImpl<Long, IridaClientDetails>
		implements IridaClientDetailsService {
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
	 * @param specification
	 * @param pageRequest
	 */
	@Override
	@PreAuthorize("permitAll()")
	public Page<IridaClientDetails> search(Specification<IridaClientDetails> specification, Pageable pageRequest) {
		return super.search(specification, pageRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("permitAll()")
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
	public IridaClientDetails create(IridaClientDetails object)
			throws ConstraintViolationException, EntityExistsException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaClientDetails read(Long object) {
		return super.read(object);
	}

	@Override
	public IridaClientDetails update(IridaClientDetails object) {
		return super.update(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Long id) throws EntityNotFoundException {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countActiveTokensForClient(IridaClientDetails client) {
		Collection<OAuth2AccessToken> findTokensByClientId = tokenStore.findTokensByClientId(client.getClientId());
		int active = findTokensByClientId.stream().mapToInt((t) -> {
			return t.isExpired() ? 0 : 1;
		}).sum();
		return active;
	}

	/**
	 * {@inheritDoc}
	 */
	public int countTokensForClient(IridaClientDetails client) {
		return tokenStore.findTokensByClientId(client.getClientId()).size();
	}

	/**
	 * {@inheritDoc}
	 */
	public void revokeTokensForClient(IridaClientDetails client) {
		Collection<OAuth2AccessToken> findTokensByClientId = tokenStore.findTokensByClientId(client.getClientId());
		for (OAuth2AccessToken token : findTokensByClientId) {
			tokenStore.removeAccessToken(token);
			if (token.getRefreshToken() != null) {
				tokenStore.removeRefreshToken(token.getRefreshToken());
			}
		}
	}

}
