package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.oauth2.IridaOAuth2AuthorizationService;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

/**
 * Service for storing and retrieving {@link IridaClientDetails} object. Implements for use with OAuth approvals.
 */
@Service("clientDetails")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class IridaClientDetailsServiceImpl extends CRUDServiceImpl<Long, IridaClientDetails>
		implements IridaClientDetailsService {
	private final IridaClientDetailsRepository clientDetailsRepository;

	private final OAuth2AuthorizationService authorizationService;

	@Autowired
	public IridaClientDetailsServiceImpl(IridaClientDetailsRepository repository,
			OAuth2AuthorizationService authorizationService, Validator validator) {
		super(repository, validator, IridaClientDetails.class);
		this.clientDetailsRepository = repository;
		this.authorizationService = authorizationService;
	}

	/**
	 * {@inheritDoc}
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
	public IridaClientDetails loadClientByClientId(String clientId) throws EntityNotFoundException {
		IridaClientDetails client = clientDetailsRepository.loadClientDetailsByClientId(clientId);
		if (client == null) {
			throw new EntityNotFoundException("Client with this clientId does not exist: " + clientId);
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
		Collection<OAuth2Authorization> accessTokenAuthorizations = ((IridaOAuth2AuthorizationService) authorizationService)
				.findAccessTokensByRegisteredClientId(client.getId().toString());
		int active = accessTokenAuthorizations.stream().mapToInt((a) -> {
			return a.getAccessToken().isActive() ? 1 : 0;
		}).sum();
		return active;
	}

	/**
	 * {@inheritDoc}
	 */
	public int countTokensForClient(IridaClientDetails client) {
		return ((IridaOAuth2AuthorizationService) authorizationService)
				.findAccessTokensByRegisteredClientId(client.getId().toString())
				.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public void revokeTokensForClient(IridaClientDetails client) {
		Collection<OAuth2Authorization> accessTokenAuthorizations = ((IridaOAuth2AuthorizationService) authorizationService)
				.findAccessTokensByRegisteredClientId(client.getId().toString());
		for (OAuth2Authorization authorization : accessTokenAuthorizations) {
			authorizationService.remove(authorization);
		}
	}

}
