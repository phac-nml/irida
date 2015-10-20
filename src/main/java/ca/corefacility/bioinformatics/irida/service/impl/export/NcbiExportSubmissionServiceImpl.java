package ca.corefacility.bioinformatics.irida.service.impl.export;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.NcbiExportSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;

@Service
public class NcbiExportSubmissionServiceImpl extends CRUDServiceImpl<Long, NcbiExportSubmission> implements
		NcbiExportSubmissionService {

	private final NcbiExportSubmissionRepository repository;

	@Autowired
	public NcbiExportSubmissionServiceImpl(NcbiExportSubmissionRepository repository, Validator validator) {
		super(repository, validator, NcbiExportSubmission.class);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#id, 'canReadExportSubmission')")
	public NcbiExportSubmission read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public NcbiExportSubmission create(NcbiExportSubmission object) throws ConstraintViolationException,
			EntityExistsException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<NcbiExportSubmission> getSubmissionsWithState(ExportUploadState state) {
		return repository.getSubmissionsWithState(ImmutableSet.of(state));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<NcbiExportSubmission> getSubmissionsWithState(Collection<ExportUploadState> states) {
		return repository.getSubmissionsWithState(states);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public NcbiExportSubmission update(Long id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		return super.update(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission('#project','canReadProject')")
	@PostFilter("hasPermission(filterObject, 'canReadExportSubmission')")
	public List<NcbiExportSubmission> getSubmissionsForProject(Project project) {
		return repository.getSubmissionsForProject(project);
	}

}
