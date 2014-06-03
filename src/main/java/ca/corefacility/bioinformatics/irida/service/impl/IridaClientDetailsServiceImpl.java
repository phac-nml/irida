package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.ClientDetailsRepository;
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
	private ClientDetailsRepository clientDetailsRepository;

	@Autowired
	public IridaClientDetailsServiceImpl(ClientDetailsRepository repository, Validator validator) {
		super(repository, validator, IridaClientDetails.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return clientDetailsRepository.loadClientDetailsByClientId(clientId);
	}

}
