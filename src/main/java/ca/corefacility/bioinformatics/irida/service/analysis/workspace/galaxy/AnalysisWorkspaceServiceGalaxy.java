package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDownloadException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.GalaxyAnalysisId;
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
	 * Builds a new AnalysisOutputFile from the given file in Galaxy.
	 * @param analysisId  The id of the analysis performed in Galaxy.
	 * @param dataset  The dataset containing the data for the AnalysisOutputFile.
	 * @return  An AnalysisOutputFile storing a local copy of the Galaxy file.
	 * @throws IOException  If there was an issue creating a local file.
	 * @throws GalaxyDownloadException  If there was an issue downloading the data from Galaxy.
	 */
	protected AnalysisOutputFile buildOutputFile(GalaxyAnalysisId analysisId,
			Dataset dataset) throws IOException, GalaxyDownloadException {
		String historyId = analysisId.getRemoteAnalysisId();
		String datasetId = dataset.getId();
		String fileName = dataset.getName();

		Path outputFile = File.createTempFile(fileName, ".dat").toPath();
		galaxyHistoriesService.downloadDatasetTo(historyId, datasetId,
				outputFile);
		
		AnalysisOutputFile analysisOutputFile = new AnalysisOutputFile(outputFile, datasetId);

		return analysisOutputFile;
	}
}