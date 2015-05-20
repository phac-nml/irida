package ca.corefacility.bioinformatics.irida.service.impl.workflow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.WorkflowNamedParametersRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

/**
 * Service for interacting with named parameter sets.
 * 
 *
 */
@Service
public class WorkflowNamedParametersServiceImpl extends CRUDServiceImpl<Long, IridaWorkflowNamedParameters> implements
		WorkflowNamedParametersService {

	private final WorkflowNamedParametersRepository repository;

	@Autowired
	public WorkflowNamedParametersServiceImpl(final WorkflowNamedParametersRepository repository,
			final Validator validator) {
		super(repository, validator, IridaWorkflowNamedParameters.class);
		this.repository = repository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public IridaWorkflowNamedParameters create(final IridaWorkflowNamedParameters parameters) {
		return super.create(parameters);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Iterable<IridaWorkflowNamedParameters> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<IridaWorkflowNamedParameters> findNamedParametersForWorkflow(UUID workflowId) {
		return repository.findByWorkflowId(workflowId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaWorkflowNamedParameters update(final Long id, final Map<String, Object> properties) {
		throw new UnsupportedOperationException("IridaWorkflowNamedParameters may not be modified.");
	}
}
