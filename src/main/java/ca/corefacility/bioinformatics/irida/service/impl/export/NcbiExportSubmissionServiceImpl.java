package ca.corefacility.bioinformatics.irida.service.impl.export;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.repositories.NcbiExportSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;

@Service
// TODO: Write permission
@PreAuthorize("permitAll()")
public class NcbiExportSubmissionServiceImpl extends CRUDServiceImpl<Long, NcbiExportSubmission> implements
		NcbiExportSubmissionService {

	@Autowired
	public NcbiExportSubmissionServiceImpl(NcbiExportSubmissionRepository repository, Validator validator) {
		super(repository, validator, NcbiExportSubmission.class);
	}
	
	@Override
	public NcbiExportSubmission read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}
	
	@Override
	public NcbiExportSubmission create(NcbiExportSubmission object) throws ConstraintViolationException,
			EntityExistsException {
		return super.create(object);
	}

}
