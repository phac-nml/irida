package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;

/**
 * Permission for whether a user can update samples in a given analysis submission.
 */
@Component
public class UpdateSamplesFromAnalysisSubmissionPermission extends
		RepositoryBackedPermission<AnalysisSubmission, Long> {

	private static final Logger logger = LoggerFactory.getLogger(UpdateSamplesFromAnalysisSubmissionPermission.class);
	private static final String PERMISSION_PROVIDED = "canUpdateSamplesFromAnalysisSubmission";

	private SampleRepository sampleRepository;
	private UpdateSamplePermission updateSamplePermission;
	private ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission;

	/**
	 * Constructs a new {@link UpdateSamplesFromAnalysisSubmissionPermission}.
	 * 
	 * @param analysisSubmissionRepository
	 *            The {@link AnalysisSubmissionRepository}.
	 * @param updateSamplePermission
	 *            The {@link UpdateSamplePermission}.
	 * @param readAnalysisSubmissionPermission
	 *            The {@link ReadAnalysisSubmissionPermission}.
	 * @param sampleRepository
	 *            The {@link SampleRepository}.
	 */
	@Autowired
	public UpdateSamplesFromAnalysisSubmissionPermission(AnalysisSubmissionRepository analysisSubmissionRepository,
			UpdateSamplePermission updateSamplePermission,
			ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission, SampleRepository sampleRepository) {
		super(AnalysisSubmission.class, Long.class, analysisSubmissionRepository);
		this.sampleRepository = sampleRepository;
		this.updateSamplePermission = updateSamplePermission;
		this.readAnalysisSubmissionPermission = readAnalysisSubmissionPermission;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	@Override
	protected boolean customPermissionAllowed(Authentication authentication, AnalysisSubmission analysisSubmission) {
		logger.trace(
				"Testing permission for [" + authentication + "] on analysis submission [" + analysisSubmission + "]");

		if (!readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)) {
			logger.trace("Permission DENIED for [" + authentication + "] on analysis submission [" + analysisSubmission
					+ "]");
			return false;
		}

		Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(analysisSubmission);

		if (updateSamplePermission.isAllowed(authentication, samples)) {
			logger.trace("Permission GRANTED for [" + authentication + "] on samples [" + samples + "]");
			return true;
		}

		logger.trace(
				"Permission DENIED for [" + authentication + "] on analysis submission [" + analysisSubmission + "]");
		return false;
	}
}
