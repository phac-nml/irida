package ca.corefacility.bioinformatics.irida.service.impl.user;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.user.Organization;
import ca.corefacility.bioinformatics.irida.repositories.user.OrganizationRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;

/**
 * Service for interacting with {@link Organization}.
 * 
 *
 */
@Service
public class OrganizationServiceImpl extends CRUDServiceImpl<Long, Organization> {

	@Autowired
	public OrganizationServiceImpl(OrganizationRepository repository, Validator validator) {
		super(repository, validator, Organization.class);
	}

}
