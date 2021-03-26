package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectMetadataTemplateJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;

/**
 * Permission for updating a {@link MetadataTemplate}
 */
@Component
public class UpdateMetadataTemplatePermission extends RepositoryBackedPermission<MetadataTemplate, Long> {

	public static final String PERMISSION_PROVIDED = "canUpdateMetadataTemplate";

	private ManageLocalProjectSettingsPermission projectPermission;
	private ProjectMetadataTemplateJoinRepository pmRepository;

	@Autowired
	public UpdateMetadataTemplatePermission(MetadataTemplateRepository repository,
			ProjectMetadataTemplateJoinRepository pmRepository, ManageLocalProjectSettingsPermission projectPermission) {
		super(MetadataTemplate.class, Long.class, repository);
		this.projectPermission = projectPermission;
		this.pmRepository = pmRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, MetadataTemplate targetDomainObject) {

		List<ProjectMetadataTemplateJoin> projects = pmRepository.getProjectsForMetadataTemplate(targetDomainObject);

		return projects.stream()
				.anyMatch(j -> projectPermission.customPermissionAllowed(authentication, j.getSubject()));
	}

}
