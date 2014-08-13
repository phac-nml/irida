package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Service for storing and reading {@link ReferenceFile} objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service
public class ReferenceFileServiceImpl extends CRUDServiceImpl<Long, ReferenceFile> implements ReferenceFileService {
	private final ProjectReferenceFileJoinRepository prfjRepository;

	@Autowired
	public ReferenceFileServiceImpl(ReferenceFileRepository repository,
			ProjectReferenceFileJoinRepository prfjRepository, Validator validator) {
		super(repository, validator, ReferenceFile.class);
		this.prfjRepository = prfjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Join<Project, ReferenceFile>> getReferenceFilesForProject(Project project) {
		return prfjRepository.findReferenceFilesForProject(project);
	}
}
