package ca.corefacility.bioinformatics.irida.ria.web.components;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

/**
 * This Spring Bean stores a temporary selection of {@link ProjectSampleAnalysisOutputInfo} and {@link AnalysisOutputFile}
 * for batch download of the selected files.
 */
@Component
@Scope("session")
public class AnalysisOutputFileDownloadManager {

	private AnalysisSubmissionService analysisSubmissionService;

	private Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> selection;

	@Autowired
	public AnalysisOutputFileDownloadManager(
			AnalysisSubmissionService analysisSubmissionService) {
		this.analysisSubmissionService = analysisSubmissionService;
		selection = new HashMap<>();
	}

	/**
	 * Set the selected {@link AnalysisOutputFile}
	 *
	 * @param outputs Selection to set
	 * @return size of the current selection
	 */
	public Long setSelection(List<ProjectSampleAnalysisOutputInfo> outputs) {
		selection.clear();
		final Map<Long, Set<ProjectSampleAnalysisOutputInfo>> submissionIdToFileIds = outputs.stream()
				.collect(Collectors.groupingBy(ProjectSampleAnalysisOutputInfo::getAnalysisSubmissionId,
						Collectors.mapping(outputInfo -> outputInfo,
								Collectors.toSet())));
		submissionIdToFileIds.forEach((submissionId, outputInfos) -> {
			final Map<Long, ProjectSampleAnalysisOutputInfo> idToOutput = outputInfos.stream()
					.collect(Collectors.toMap(ProjectSampleAnalysisOutputInfo::getAnalysisOutputFileId, info -> info));
			final AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
			final Analysis analysis = submission.getAnalysis();
			final Set<AnalysisOutputFile> outputFiles = analysis.getAnalysisOutputFiles();
			final Set<AnalysisOutputFile> filteredOutputFiles = outputFiles.stream()
					.filter(outputFile -> idToOutput.containsKey(outputFile.getId()))
					.collect(Collectors.toSet());
			filteredOutputFiles.forEach((analysisOutputFile -> {
				selection.put(idToOutput.get(analysisOutputFile.getId()), analysisOutputFile);
			}));

		});
		return (long) selection.size();
	}

	/**
	 * Get the current selection of {@link AnalysisOutputFile}.
	 *
	 * @return Map of {@link ProjectSampleAnalysisOutputInfo} to {@link AnalysisOutputFile}
	 */
	public Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> getSelection() {
		return selection;
	}
}
