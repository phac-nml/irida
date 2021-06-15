package ca.corefacility.bioinformatics.irida.security.permissions.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.NcbiExportSubmissionRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Whether or not a {@link User} can read a given {@link NcbiExportSubmission}
 */
@Component
public class ReadExportSubmissionPermission extends RepositoryBackedPermission<NcbiExportSubmission, Long> {

	private static final String PERMISSION_PROVIDED = "canReadExportSubmission";

	private final ReadProjectPermission readProjectPermission;

	/**
	 * Construct an instance of {@link ReadExportSubmissionPermission}.
	 * 
	 * @param submissionRepository
	 *            A {@link NcbiExportSubmissionRepository}.
	 * @param readProjectPermission
	 *            A {@link ReadProjectPermission}.
	 */
	@Autowired
	public ReadExportSubmissionPermission(final NcbiExportSubmissionRepository submissionRepository,
			final ReadProjectPermission readProjectPermission) {
		super(NcbiExportSubmission.class, Long.class, submissionRepository);
		this.readProjectPermission = readProjectPermission;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication, final NcbiExportSubmission sub) {
		Project project = sub.getProject();
		return readProjectPermission.customPermissionAllowed(authentication, project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
