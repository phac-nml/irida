package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataFieldRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Service for storing and reading {@link MetadataTemplate}s
 */
@Service
public class MetadataTemplateServiceImpl extends CRUDServiceImpl<Long, MetadataTemplate>
		implements MetadataTemplateService {

	private MetadataTemplateRepository metadataTemplateRepository;
	private MetadataFieldRepository fieldRepository;
	private MetadataRestrictionRepository metadataRestrictionRepository;
	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private UserGroupProjectJoinRepository userGroupProjectJoinRepository;

	@Autowired
	public MetadataTemplateServiceImpl(MetadataTemplateRepository repository, MetadataFieldRepository fieldRepository,
			Validator validator, MetadataRestrictionRepository metadataRestrictionRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			UserGroupProjectJoinRepository userGroupProjectJoinRepository) {
		super(repository, validator, MetadataTemplate.class);
		this.metadataTemplateRepository = repository;
		this.fieldRepository = fieldRepository;
		this.metadataRestrictionRepository = metadataRestrictionRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.userGroupProjectJoinRepository = userGroupProjectJoinRepository;
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
	public MetadataTemplate createMetadataTemplateInProject(MetadataTemplate template, Project project) {
		template.setProject(project);
		template = create(template);

		return template;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public List<MetadataTemplate> getMetadataTemplatesForProject(Project project) {
		return metadataTemplateRepository.getMetadataTemplatesForProject(project);
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

	@PreAuthorize("permitAll()")
	@Override
	public MetadataTemplateField updateMetadataField(MetadataTemplateField field) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("permitAll()")
	public Set<MetadataEntry> convertMetadataStringsToSet(Map<String, MetadataEntry> metadataMap) {
		Set<MetadataEntry> metadata = new HashSet<>();

		metadataMap.entrySet().forEach(e -> {
			MetadataTemplateField field = readMetadataFieldByLabel(e.getKey());

			// if not, create a new one
			if (field == null) {
				field = new MetadataTemplateField(e.getKey(), "text");
				field = saveMetadataField(field);
			}

			MetadataEntry entry = e.getValue();

			entry.setField(field);

			metadata.add(entry);

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

	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public MetadataRestriction getMetadataRestrictionForFieldAndProject(Project project, MetadataTemplateField field) {
		return metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field);
	}

	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	@Transactional
	public List<MetadataRestriction> getMetadataRestrictionsForProject(Project project) {
		return metadataRestrictionRepository.getRestrictionForProject(project);
	}

	@PreAuthorize("hasPermission(#project, 'isProjectOwner')")
	@Override
	@Transactional
	public MetadataRestriction setMetadataRestriction(Project project, MetadataTemplateField field,
			ProjectMetadataRole role) {
		MetadataRestriction metadataRestrictionForFieldAndProject = getMetadataRestrictionForFieldAndProject(project,
				field);

		if (metadataRestrictionForFieldAndProject != null) {
			metadataRestrictionForFieldAndProject.setLevel(role);
		} else {
			metadataRestrictionForFieldAndProject = new MetadataRestriction(project, field, role);
		}

		return metadataRestrictionRepository.save(metadataRestrictionForFieldAndProject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#template, 'canReadMetadataTemplate')")
	public List<MetadataTemplateField> getPermittedFieldsForTemplate(MetadataTemplate template) {
		List<MetadataTemplateField> fieldsForTemplate = fieldRepository.getMetadataFieldsForTemplate(template);

		List<MetadataTemplateField> permittedFieldsForCurrentUser = getPermittedFieldsForCurrentUser(
				template.getProject(), true);

		return fieldsForTemplate.stream()
				.filter(f -> permittedFieldsForCurrentUser.contains(f))
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	private List<MetadataTemplateField> getPermittedFieldsForRole(Project project, ProjectMetadataRole role,
			boolean includeTemplateFields) {
		//get all fields for the project
		List<MetadataTemplateField> metadataFieldsForProject = fieldRepository.getMetadataFieldsForProject(project);

		if (includeTemplateFields) {
			//add all the metadata template fields to the list of fields to restrict
			List<MetadataTemplate> templatesForProject = getMetadataTemplatesForProject(project);
			for (MetadataTemplate template : templatesForProject) {

				List<MetadataTemplateField> templateFields = fieldRepository.getMetadataFieldsForTemplate(template);
				for (MetadataTemplateField field : templateFields) {
					if (!metadataFieldsForProject.contains(field)) {
						metadataFieldsForProject.add(field);
					}
				}
			}
		}

		//get all restrictions for the project
		List<MetadataRestriction> restrictionForProject = metadataRestrictionRepository
				.getRestrictionForProject(project);

		//collect the fields into a map
		Map<Long, MetadataRestriction> restrictionMap = restrictionForProject.stream()
				.collect(Collectors.toMap(metadataRestriction -> metadataRestriction.getField().getId(), field -> field));

		//for each field to check
		List<MetadataTemplateField> filteredFields = metadataFieldsForProject.stream().filter(field -> {
			//if the restriction map contains the field
			if (restrictionMap.containsValue(field.getId())) {
				MetadataRestriction metadataRestriction = restrictionMap.get(field);
				ProjectMetadataRole restrictionRole = metadataRestriction.getLevel();

				//compare the restriction level to the given role.  If it's greater or equal, we're good
				if (role.getLevel() >= restrictionRole.getLevel()) {
					return true;
				}

				//if it's less, filter out the field
				return false;
			} else {
				//if there's no restriction set for the field, all users can view
				return true;
			}

		}).collect(Collectors.toList());

		return filteredFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public List<MetadataTemplateField> getPermittedFieldsForCurrentUser(Project project,
			boolean includeTemplateFields) {
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		final User loggedInUser = userRepository.loadUserByUsername(username);

		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, loggedInUser);

		List<UserGroupProjectJoin> groupsForProjectAndUser = userGroupProjectJoinRepository
				.findGroupsForProjectAndUser(project, loggedInUser);

		ProjectMetadataRole metadataRole = ProjectMetadataRole.getMaxRoleForProjectAndGroups(projectJoinForUser,
				groupsForProjectAndUser);

		//if the user isn't on the project and the user is an admin, give them project owner powers
		if (metadataRole == null && loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN)) {
			metadataRole = ProjectMetadataRole.LEVEL_4;
		}

		List<MetadataTemplateField> permittedFieldsForRole = getPermittedFieldsForRole(project, metadataRole,
				includeTemplateFields);

		return permittedFieldsForRole;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public MetadataTemplate getDefaultTemplateForProject(Project project) {
		List<MetadataTemplate> metadataTemplatesForProject = metadataTemplateRepository
				.getMetadataTemplatesForProject(project);

		Optional<MetadataTemplate> templateOptional = metadataTemplatesForProject.stream()
				.filter(MetadataTemplate::isProjectDefault)
				.findFirst();

		MetadataTemplate template = null;
		if (templateOptional.isPresent()) {
			template = templateOptional.get();
		}

		return template;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canManageLocalProjectSettings')")
	@Override
	@Transactional
	public MetadataTemplate updateDefaultMetadataTemplateForProject(Project project, MetadataTemplate template) {

		MetadataTemplate originalDefault = getDefaultTemplateForProject(project);
		if (originalDefault != null) {
			originalDefault.setProjectDefault(false);
			metadataTemplateRepository.save(originalDefault);
		}

		template.setProjectDefault(true);
		metadataTemplateRepository.save(template);

		return template;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canManageLocalProjectSettings')")
	@Override
	@Transactional
	public void removeDefaultMetadataTemplateForProject(Project project) {
		MetadataTemplate originalDefault = getDefaultTemplateForProject(project);
		if (originalDefault != null) {
			originalDefault.setProjectDefault(false);
			metadataTemplateRepository.save(originalDefault);
		}
	}

}
