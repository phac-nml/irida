package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerDownloadException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.RemoteWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;

/**
 * A service for performing tasks for analysis in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <R> The type of RemoteWorkflow to use.
 * @param <S> The AnalysisSubmissionGalaxy to prepare and execute.
 * @param <A>  The Analysis object to return as a result.
 */
public abstract class AnalysisWorkspaceServiceGalaxy<R extends RemoteWorkflowGalaxy,
	S extends AnalysisSubmissionGalaxy<R>, A extends Analysis> 
	implements AnalysisWorkspaceService<S,PreparedWorkflowGalaxy,A> {
	
	protected GalaxyHistoriesService galaxyHistoriesService;
	
	/**
	 * Builds a new AnalysisWorkspaceServiceGalaxy with the given service.
	 * @param galaxyHistoriesService  A GalaxyHistoriesService for interacting with Galaxy Histories.
	 */
	public AnalysisWorkspaceServiceGalaxy(
			GalaxyHistoriesService galaxyHistoriesService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String prepareAnalysisWorkspace(S analysisSubmission)
			throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getInputFiles(), "inputFiles are null");
		checkArgument(analysisSubmission.getRemoteAnalysisId() == null, "analysis id should be null");
		
		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();
		
		return workflowHistory.getId();
	}

	/**
	 * Builds a new AnalysisOutputFile from the given file in Galaxy.
	 * @param analysisId  The id of the analysis performed in Galaxy.
	 * @param dataset  The dataset containing the data for the AnalysisOutputFile.
	 * @return  An AnalysisOutputFile storing a local copy of the Galaxy file.
	 * @throws IOException  If there was an issue creating a local file.
	 * @throws ExecutionManagerDownloadException  If there was an issue downloading the data from Galaxy.
	 */
	protected AnalysisOutputFile buildOutputFile(String analysisId,
			Dataset dataset) throws IOException, ExecutionManagerDownloadException {
		String datasetId = dataset.getId();
		String fileName = dataset.getName();

		Path outputFile = Files.createTempFile(fileName, ".dat");
		galaxyHistoriesService.downloadDatasetTo(analysisId, datasetId,
				outputFile);
		
		AnalysisOutputFile analysisOutputFile = new AnalysisOutputFile(outputFile, datasetId);

		return analysisOutputFile;
	}
}