package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectMetadataTemplateJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
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

	private ProjectMetadataTemplateJoinRepository pmtRepository;
	private MetadataFieldRepository fieldRepository;
	private MetadataRestrictionRepository metadataRestrictionRepository;
	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;

	@Autowired
	public MetadataTemplateServiceImpl(MetadataTemplateRepository repository,
			ProjectMetadataTemplateJoinRepository pmtRepository, MetadataFieldRepository fieldRepository,
			Validator validator, MetadataRestrictionRepository metadataRestrictionRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository) {
		super(repository, validator, MetadataTemplate.class);
		this.pmtRepository = pmtRepository;
		this.fieldRepository = fieldRepository;
		this.metadataRestrictionRepository = metadataRestrictionRepository;
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
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
		return fieldRepository.findById(id)
				.orElse(null);
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
			return fieldRepository.findById(Long.parseLong(stripped))
					.orElse(null);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("permitAll()")
	public Set<MetadataEntry> convertMetadataStringsToSet(Map<String, MetadataEntry> metadataMap) {
		Set<MetadataEntry> metadata = new HashSet<>();

		metadataMap.entrySet()
				.forEach(e -> {
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
	public MetadataRestriction setMetadataRestriction(Project project, MetadataTemplateField field, ProjectRole role) {
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
	@PreAuthorize("hasPermission(#project, 'isProjectOwner')")
	@Override
	public List<MetadataTemplateField> getPermittedFieldsForRole(Project project, ProjectRole role) {
		//get all fields for the project
		List<MetadataTemplateField> metadataFieldsForProject = fieldRepository.getMetadataFieldsForProject(project);

		//get all restrictions for the project
		List<MetadataRestriction> restrictionForProject = metadataRestrictionRepository.getRestrictionForProject(
				project);

		//collect the fields into a map
		Map<MetadataTemplateField, MetadataRestriction> restrictionMap = restrictionForProject.stream()
				.collect(Collectors.toMap(MetadataRestriction::getField, field -> field));

		//for each field to check
		List<MetadataTemplateField> filteredFields = metadataFieldsForProject.stream()
				.filter(field -> {
					//if the restriction map contains the field
					if (restrictionMap.containsKey(field)) {
						MetadataRestriction metadataRestriction = restrictionMap.get(field);
						ProjectRole restrictionRole = metadataRestriction.getLevel();

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

				})
				.collect(Collectors.toList());

		return filteredFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public List<MetadataTemplateField> getPermittedFieldsForCurrentUser(Project project) {
		final UserDetails loggedInDetails = (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		User loggedInUser = userRepository.loadUserByUsername(loggedInDetails.getUsername());
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, loggedInUser);

		ProjectRole projectRole;

		//if the user isn't on the project and the user is an admin, give them project owner powers
		if (projectJoinForUser == null && loggedInUser.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			projectRole = ProjectRole.PROJECT_OWNER;
		} else {
			projectRole = projectJoinForUser.getProjectRole();
		}

		List<MetadataTemplateField> permittedFieldsForRole = getPermittedFieldsForRole(project, projectRole);

		return permittedFieldsForRole;
	}

}
