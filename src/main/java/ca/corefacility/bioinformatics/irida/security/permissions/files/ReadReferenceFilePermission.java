package ca.corefacility.bioinformatics.irida.security.permissions.files;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.ReadAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;

/**
 * Permission testing if a given user can read a given reference file.
 * 
 *
 */
@Component
public class ReadReferenceFilePermission extends RepositoryBackedPermission<ReferenceFile, Long> {

	public static final String PERMISSION_PROVIDED = "canReadReferenceFile";

	private final ReadProjectPermission readProjectPermission;
	private final ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission;
	private final ProjectReferenceFileJoinRepository prfRepository;
	private final AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	public ReadReferenceFilePermission(final ReferenceFileRepository referenceFileRepository,
			final ProjectReferenceFileJoinRepository prfRepository,
			final AnalysisSubmissionRepository analysisSubmissionRepository,
			final ReadProjectPermission readProjectPermission,
			final ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission) {
		super(ReferenceFile.class, Long.class, referenceFileRepository);
		this.prfRepository = prfRepository;
		this.readProjectPermission = readProjectPermission;
		this.readAnalysisSubmissionPermission = readAnalysisSubmissionPermission;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
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

		Set<AnalysisSubmission> analysisSubmissions = analysisSubmissionRepository
				.findByReferenceFile(targetDomainObject);

		// Allowed to read reference file if they have permission on an analysis
		// submission using this file, or if they have permission on a project
		// with this reference file.
		return (analysisSubmissions.size() > 0
				&& readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmissions))
				|| findProjectsForReferenceFile.stream()
						.anyMatch(j -> readProjectPermission.isAllowed(authentication, j.getSubject()));
	}
}
