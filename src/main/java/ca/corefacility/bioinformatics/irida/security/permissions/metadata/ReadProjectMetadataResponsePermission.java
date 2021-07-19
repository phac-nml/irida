package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;

/**
 * Permission for checking that a user should have access to the given {@link MetadataTemplateField}s in a {@link ProjectMetadataResponse}
 */
@Component
public class ReadProjectMetadataResponsePermission implements BasePermission<ProjectMetadataResponse> {

	public static final String PERMISSION_PROVIDED = "readProjectMetadataResponse";

	private UserRepository userRepository;
	private ProjectUserJoinRepository projectUserJoinRepository;
	private MetadataRestrictionRepository metadataRestrictionRepository;

	@Autowired
	public ReadProjectMetadataResponsePermission(UserRepository userRepository,
			ProjectUserJoinRepository projectUserJoinRepository,
			MetadataRestrictionRepository metadataRestrictionRepository) {
		this.userRepository = userRepository;
		this.projectUserJoinRepository = projectUserJoinRepository;
		this.metadataRestrictionRepository = metadataRestrictionRepository;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	@Override
	public boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		//ensure the object we're checking is a ProjectMetadataResponse
		if (!(targetDomainObject instanceof ProjectMetadataResponse)) {
			throw new IllegalArgumentException(
					"Object is not a ProjectMetadataResponse: " + targetDomainObject.getClass());
		}
		ProjectMetadataResponse metadataResponse = (ProjectMetadataResponse) targetDomainObject;

		//get the user & projec
		User user = userRepository.loadUserByUsername(authentication.getName());
		Project project = metadataResponse.getProject();
		ProjectUserJoin projectJoinForUser = projectUserJoinRepository.getProjectJoinForUser(project, user);

		//get the user's role on the project
		ProjectRole userProjectRole;
		if (projectJoinForUser != null) {
			userProjectRole = projectJoinForUser.getProjectRole();
		} else if (user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			//if the user isn't on the project but is an admin, treat them as a project owner
			userProjectRole = ProjectRole.PROJECT_OWNER;
		} else {
			//if the user is not otherwise on the project, they shouldn't be able to read anything
			return false;
		}

		//get the metadata restrictions on the project
		List<MetadataRestriction> restrictionForProject = metadataRestrictionRepository.getRestrictionForProject(
				project);
		Map<MetadataTemplateField, MetadataRestriction> restrictionMap = restrictionForProject.stream()
				.collect(Collectors.toMap(MetadataRestriction::getField, field -> field));

		//go through the metadata being returned and get a distinct collection of the fields
		Map<Long, Set<MetadataEntry>> metadata = metadataResponse.getMetadata();
		Set<MetadataTemplateField> fields = new HashSet<>();
		for (Set<MetadataEntry> entries : metadata.values()) {
			for (MetadataEntry entry : entries) {
				fields.add(entry.getField());
			}
		}

		/*
		 * for each field check if the set of fields contain any they shouldn't be able to read.
		 * this will return true if the user is allowed to read all the fields in the set.
		 */
		boolean allFieldsValid = fields.stream()
				.filter(field -> {
					//if we have a restriction on a field, compare it against the user's role on the project
					if (restrictionMap.containsKey(field)) {
						MetadataRestriction metadataRestriction = restrictionMap.get(field);
						ProjectRole restrictionRole = metadataRestriction.getLevel();

						/*
						 * Compare the restriction level to the user's role.  If user's role is less, return the unauthorized field.
						 */
						return userProjectRole.getLevel() < restrictionRole.getLevel();
					} else {
						//if there's no restriction set for the field, all users can view
						return false;
					}

				})
				.findAny()
				.isEmpty();

		return allFieldsValid;
	}
}
