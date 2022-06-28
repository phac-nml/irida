package ca.corefacility.bioinformatics.irida.security.permissions.files;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;

/**
 * Confirms that the authenticated user is allowed to modify a reference file.
 * 
 * 
 */
@Component
public class UpdateReferenceFilePermission extends RepositoryBackedPermission<ReferenceFile, Long> {

	public static final String PERMISSION_PROVIDED = "canUpdateReferenceFile";

	private final ProjectReferenceFileJoinRepository prfRepository;
	private final ProjectOwnerPermission projectOwnerPermission;

	@Autowired
	public UpdateReferenceFilePermission(final ReferenceFileRepository referenceFileRepository,
			final ProjectReferenceFileJoinRepository prfRepository,
			final ProjectOwnerPermission projectOwnerPermission) {
		super(ReferenceFile.class, Long.class, referenceFileRepository);
		this.prfRepository = prfRepository;
		this.projectOwnerPermission = projectOwnerPermission;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, ReferenceFile targetDomainObject) {
		// get the projects for the file
		List<Join<Project, ReferenceFile>> findProjectsForReferenceFile = prfRepository
				.findProjectsForReferenceFile(targetDomainObject);

		return findProjectsForReferenceFile.stream()
				.anyMatch(j -> projectOwnerPermission.isAllowed(authentication, j.getSubject()));
	}

}
