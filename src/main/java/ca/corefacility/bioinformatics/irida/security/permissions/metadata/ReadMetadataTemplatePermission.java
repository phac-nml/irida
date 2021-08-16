package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataTemplateRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;

/**
 * Permission for reading {@link MetadataTemplate}
 */
@Component
public class ReadMetadataTemplatePermission extends RepositoryBackedPermission<MetadataTemplate, Long> {

	public static final String PERMISSION_PROVIDED = "canReadMetadataTemplate";

	private ReadProjectPermission projectPermission;

	@Autowired
	public ReadMetadataTemplatePermission(MetadataTemplateRepository repository,
			ReadProjectPermission projectPermission) {
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

		return projectPermission.isAllowed(authentication, targetDomainObject.getProject());
	}

}
