package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectMetadataTemplateJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataFieldRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Service for storing and reading {@link MetadataTemplate}s
 */
@Service
public class MetadataTemplateServiceImpl extends CRUDServiceImpl<Long, MetadataTemplate>
		implements MetadataTemplateService {

	private ProjectMetadataTemplateJoinRepository pmtRepository;
	private MetadataFieldRepository fieldRepository;

	@Autowired
	public MetadataTemplateServiceImpl(MetadataTemplateRepository repository,
			ProjectMetadataTemplateJoinRepository pmtRepository, MetadataFieldRepository fieldRepository,
			Validator validator) {
		super(repository, validator, MetadataTemplate.class);
		this.pmtRepository = pmtRepository;
		this.fieldRepository = fieldRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#id, 'canReadMetadataTemplate')")
	@Override
	public MetadataTemplate read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#template, 'canUpdateMetadataTemplate')")
	@Override
	public MetadataTemplate updateMetadataTemplateInProject(MetadataTemplate template) {
		return super.update(template);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#id, 'canUpdateMetadataTemplate')")
	@Override
	public void deleteMetadataTemplateFromProject(Project project, Long id) throws EntityNotFoundException {
		super.delete(id);
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

	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplateField readMetadataField(Long id) {
		return fieldRepository.findById(id).orElse(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplateField readMetadataFieldByLabel(String label) {
		return fieldRepository.findMetadataFieldByLabel(label);
	}

	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplateField readMetadataFieldByKey(String key) {
		if (key.startsWith(StaticMetadataTemplateField.STATIC_FIELD_PREFIX)) {
			String stripped = key.replaceFirst(StaticMetadataTemplateField.STATIC_FIELD_PREFIX, "");
			return fieldRepository.findMetadataFieldByStaticId(stripped);
		} else {
			String stripped = key.replaceFirst(MetadataTemplateField.DYNAMIC_FIELD_PREFIX, "");
			return fieldRepository.findById(Long.parseLong(stripped)).orElse(null);
		}
	}

	@PreAuthorize("permitAll()")
	@Override
	public List<StaticMetadataTemplateField> getStaticMetadataFields() {
		return fieldRepository.findStaticMetadataFields();
	}

	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplateField saveMetadataField(MetadataTemplateField field) {
		if (field.getId() != null) {
			throw new IllegalArgumentException("Cannot save a MetadataField that has an ID");
		}

		return fieldRepository.save(field);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("permitAll()")
	public List<MetadataTemplateField> getAllMetadataFieldsByQueryString(String query) {
		return fieldRepository.findAllMetadataFieldsByLabelQuery(query);
	}

	@Override
	@Transactional
	@PreAuthorize("permitAll()")
	public Map<MetadataTemplateField, MetadataEntry> getMetadataMap(Map<String, MetadataEntry> metadataMap) {
		Map<MetadataTemplateField, MetadataEntry> metadata = new HashMap<>();
		metadataMap.entrySet().forEach(e -> {

			// get the metadatatemplatefield if it exists
			MetadataTemplateField field = readMetadataFieldByLabel(e.getKey());

			// if not, create a new one
			if (field == null) {
				field = new MetadataTemplateField(e.getKey(), "text");
				field = saveMetadataField(field);
			}

			metadata.put(field, e.getValue());
		});

		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project project) {
		return fieldRepository.getMetadataFieldsForProject(project);
	}

}
