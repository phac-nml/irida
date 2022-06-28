package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
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

	@Autowired
	public UpdateMetadataTemplatePermission(MetadataTemplateRepository repository,
			ManageLocalProjectSettingsPermission projectPermission) {
		super(MetadataTemplate.class, Long.class, repository);
		this.projectPermission = projectPermission;
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

		return projectPermission.customPermissionAllowed(authentication, targetDomainObject.getProject());
	}

}
