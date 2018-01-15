package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContentsProvenance;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;

/**
 * Class to create new {@link JobError} objects using a Galaxy {@link HistoriesClient}, {@link ToolsClient} and
 * {@link JobsClient} if an error occurred during an Galaxy pipeline {@link AnalysisSubmission}.
 */
public class GalaxyJobErrorsService {

	private final HistoriesClient historiesClient;
	private final ToolsClient toolsClient;
	private final JobsClient jobsClient;

	public GalaxyJobErrorsService(HistoriesClient historiesClient, ToolsClient toolsClient, JobsClient jobsClient) {
		this.historiesClient = historiesClient;
		this.toolsClient = toolsClient;
		this.jobsClient = jobsClient;
	}

	/**
	 * Get any {@link JobError} associated with an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmission {@link AnalysisSubmission} to search for job failures
	 * @return List of {@link JobError} objects associated with {@link AnalysisSubmission}
	 */
	public List<JobError> createNewJobErrors(AnalysisSubmission analysisSubmission) {
		String historyId = analysisSubmission.getRemoteAnalysisId();
		HistoryDetails historyDetails = historiesClient.showHistory(historyId);
		List<String> erroredDatasetIds = historyDetails.getStateIds()
				.get(GalaxyWorkflowState.ERROR.toString());
		List<HistoryContentsProvenance> provenances = erroredDatasetIds.stream()
				.map((x) -> historiesClient.showProvenance(historyId, x))
				.collect(Collectors.toList());
		Map<String, List<HistoryContentsProvenance>> jobIdProvenancesMap = provenances.stream()
				.collect(Collectors.groupingBy(HistoryContentsProvenance::getJobId));
		List<JobError> jobErrors = new ArrayList<>();
		for (Map.Entry<String, List<HistoryContentsProvenance>> entry : jobIdProvenancesMap.entrySet()) {
			String jobId = entry.getKey();
			JobDetails jobDetails = jobsClient.showJob(jobId);
			HistoryContentsProvenance p = entry.getValue()
					.iterator()
					.next();
			Tool tool = toolsClient.showTool(p.getToolId());
			jobErrors.add(new JobError(analysisSubmission, jobDetails, p, tool));
		}
		return jobErrors;
	}

}
