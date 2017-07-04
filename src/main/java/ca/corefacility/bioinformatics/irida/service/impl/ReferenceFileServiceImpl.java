package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.files.ReadReferenceFilePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.files.UpdateReferenceFilePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Service for storing and reading {@link ReferenceFile} objects
 * 
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, '" + ReadReferenceFilePermission.PERMISSION_PROVIDED
			+ "')")
	public ReferenceFile read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, '" + ReadProjectPermission.PERMISSION_PROVIDED
			+ "')")
	public List<Join<Project, ReferenceFile>> getReferenceFilesForProject(Project project) {
		return prfjRepository.findReferenceFilesForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public ReferenceFile create(ReferenceFile object) throws ConstraintViolationException, EntityExistsException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, '" + UpdateReferenceFilePermission.PERMISSION_PROVIDED
			+ "')")
	public void delete(Long id) throws EntityNotFoundException {
		super.delete(id);
	}

}
