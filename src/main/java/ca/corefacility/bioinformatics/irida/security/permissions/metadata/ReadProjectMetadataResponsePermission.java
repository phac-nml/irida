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

@Component
public class ReadProjectMetadataResponsePermission implements BasePermission<ProjectMetadataResponse> {

	public static final String PERMISSION_PROVIDED = "readProjectMetadataResponse";

	UserRepository userRepository;
	ProjectUserJoinRepository projectUserJoinRepository;
	MetadataRestrictionRepository metadataRestrictionRepository;

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
		if (!(targetDomainObject instanceof ProjectMetadataResponse)) {
			throw new IllegalArgumentException(
					"Object is not a ProjectMetadataResponse: " + targetDomainObject.getClass());
		}

		ProjectMetadataResponse metadataResponse = (ProjectMetadataResponse) targetDomainObject;

		User user = userRepository.loadUserByUsername(authentication.getName());

		Project project = metadataResponse.getProject();

		ProjectUserJoin projectJoinForUser = projectUserJoinRepository.getProjectJoinForUser(project, user);

		ProjectRole userProjectRole;
		if (projectJoinForUser != null) {
			userProjectRole = projectJoinForUser.getProjectRole();
		} else if (user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			userProjectRole = ProjectRole.PROJECT_OWNER;
		} else {
			return false;
		}

		List<MetadataRestriction> restrictionForProject = metadataRestrictionRepository.getRestrictionForProject(
				project);

		Map<MetadataTemplateField, MetadataRestriction> restrictionMap = restrictionForProject.stream()
				.collect(Collectors.toMap(MetadataRestriction::getField, field -> field));

		Map<Long, Set<MetadataEntry>> metadata = metadataResponse.getMetadata();

		Set<MetadataTemplateField> fields = new HashSet<>();

		for (Set<MetadataEntry> entries : metadata.values()) {
			for (MetadataEntry entry : entries) {
				fields.add(entry.getField());
			}
		}

		//for each field to check
		Optional<MetadataTemplateField> filteredField = fields.stream()
				.filter(field -> {
					//if the restriction map contains the field
					if (restrictionMap.containsKey(field)) {
						MetadataRestriction metadataRestriction = restrictionMap.get(field);
						ProjectRole restrictionRole = metadataRestriction.getLevel();

						//compare the restriction level to the given role.  If it's greater or equal, we're good
						if (userProjectRole.getLevel() >= restrictionRole.getLevel()) {
							return false;
						}

						//if it's less, filter out the field
						return true;
					} else {
						//if there's no restriction set for the field, all users can view
						return false;
					}

				})
				.findAny();

		return filteredField.isEmpty();
	}
}
