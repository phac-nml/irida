package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;

/**
 * Permission testing if a given user can read a given reference file.
 * 
 *
 */
@Component
public class ReadReferenceFilePermission extends BasePermission<ReferenceFile, Long> {

	public static final String PERMISSION_PROVIDED = "canReadReferenceFile";

	private final ReadProjectPermission readProjectPermission;
	private final ProjectReferenceFileJoinRepository prfRepository;

	@Autowired
	public ReadReferenceFilePermission(final ReferenceFileRepository referenceFileRepository,
			final ProjectReferenceFileJoinRepository prfRepository, final ReadProjectPermission readProjectPermission) {
		super(ReferenceFile.class, Long.class, referenceFileRepository);
		this.prfRepository = prfRepository;
		this.readProjectPermission = readProjectPermission;
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
				.anyMatch(j -> readProjectPermission.isAllowed(authentication, j.getSubject()));
	}
}
