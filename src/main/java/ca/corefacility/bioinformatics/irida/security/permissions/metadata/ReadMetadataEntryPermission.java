package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Permission for reading individual {@link MetadataEntry} for a sample
 */
@Component
public class ReadMetadataEntryPermission extends RepositoryBackedPermission<MetadataEntry, Long> {

	public static final String PERMISSION_PROVIDED = "canReadMetadataEntry";

	ProjectSampleJoinRepository projectSampleJoinRepository;
	MetadataRestrictionRepository restrictionRepository;
	ProjectUserJoinRepository projectUserJoinRepository;
	UserGroupProjectJoinRepository userGroupProjectJoinRepository;

	UserRepository userRepository;

	@Autowired
	public ReadMetadataEntryPermission(MetadataEntryRepository repository,
			ProjectSampleJoinRepository projectSampleJoinRepository,
			MetadataRestrictionRepository restrictionRepository, ProjectUserJoinRepository projectUserJoinRepository,
			UserRepository userRepository, UserGroupProjectJoinRepository userGroupProjectJoinRepository) {
		super(MetadataEntry.class, Long.class, repository);
		this.projectSampleJoinRepository = projectSampleJoinRepository;
		this.restrictionRepository = restrictionRepository;
		this.projectUserJoinRepository = projectUserJoinRepository;
		this.userRepository = userRepository;
		this.userGroupProjectJoinRepository = userGroupProjectJoinRepository;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	@Override
	protected boolean customPermissionAllowed(Authentication authentication, MetadataEntry targetDomainObject) {
		Sample sample = targetDomainObject.getSample();

		final User user = userRepository.loadUserByUsername(authentication.getName());

		List<Join<Project, Sample>> projectForSample = projectSampleJoinRepository.getProjectForSample(sample);

		MetadataTemplateField field = targetDomainObject.getField();

		for (Join<Project, Sample> join : projectForSample) {
			Project project = join.getSubject();

			MetadataRestriction restrictionForFieldAndProject = restrictionRepository.getRestrictionForFieldAndProject(
					project, field);

			ProjectUserJoin projectJoinForUser = projectUserJoinRepository.getProjectJoinForUser(project, user);
			List<UserGroupProjectJoin> groupsForProjectAndUser = userGroupProjectJoinRepository.findGroupsForProjectAndUser(
					project, user);

			ProjectMetadataRole metadataRole = ProjectMetadataRole.getMaxRoleForProjectAndGroups(projectJoinForUser,
					groupsForProjectAndUser);

			if (metadataRole != null) {
				//if there's no restriction, add it at the base level
				if (restrictionForFieldAndProject == null) {
					restrictionForFieldAndProject = new MetadataRestriction(project, field,
							ProjectMetadataRole.LEVEL_1);
				}

				if (metadataRole.getLevel() >= restrictionForFieldAndProject.getLevel()
						.getLevel()) {
					return true;
				}
			}
		}

		return false;
	}
	
}