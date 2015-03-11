package ca.corefacility.bioinformatics.irida.service.impl.workflow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
	public List<IridaWorkflowNamedParameters> findNamedParametersForWorkflow(UUID workflowId) {
		return repository.findByWorkflowId(workflowId);
	}

	@Override
	public IridaWorkflowNamedParameters update(final Long id, final Map<String, Object> properties) {
		throw new UnsupportedOperationException("IridaWorkflowNamedParameters may not be modified.");
	}
}
