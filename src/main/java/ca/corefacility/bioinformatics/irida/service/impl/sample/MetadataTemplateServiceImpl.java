package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectMetadataTemplateJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

@Service
public class MetadataTemplateServiceImpl extends CRUDServiceImpl<Long, MetadataTemplate>
		implements MetadataTemplateService {

	private ProjectMetadataTemplateJoinRepository pmtRepository;

	@Autowired
	public MetadataTemplateServiceImpl(MetadataTemplateRepository repository,
			ProjectMetadataTemplateJoinRepository pmtRepository, Validator validator) {
		super(repository, validator, MetadataTemplate.class);
		this.pmtRepository = pmtRepository;
	}

	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplate read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'isProjectOwner')")
	@Transactional
	public ProjectMetadataTemplateJoin createMetadataTemplateInProject(MetadataTemplate template, Project project) {
		template = create(template);

		ProjectMetadataTemplateJoin join = pmtRepository.save(new ProjectMetadataTemplateJoin(project, template));

		return join;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project) {
		return pmtRepository.getMetadataTemplatesForProject(project);
	}

}
