package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.repositories.NcbiExportSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.NcbiExportSubmissionService;

@Service
// TODO: Write permission
@PreAuthorize("permitAll()")
public class NcbiExportSubmissionServiceImpl extends CRUDServiceImpl<Long, NcbiExportSubmission> implements
		NcbiExportSubmissionService {

	@Autowired
	public NcbiExportSubmissionServiceImpl(NcbiExportSubmissionRepository repository, Validator validator) {
		super(repository, validator, NcbiExportSubmission.class);
	}

}
