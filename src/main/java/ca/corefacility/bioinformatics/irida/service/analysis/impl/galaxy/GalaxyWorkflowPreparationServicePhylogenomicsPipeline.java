package ca.corefacility.bioinformatics.irida.service.analysis.impl.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.AnalysisSubmissionGalaxyPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Prepares a Phylogenomics Pipeline for execution in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowPreparationServicePhylogenomicsPipeline {
	
	private GalaxyHistoriesService galaxyHistoriesService;
	
	public class GalaxyPreparedWorkflow {
		private CollectionResponse sequenceFilesCollection;
		private Dataset referenceDataset;
		private History workflowHistory;
		
		public GalaxyPreparedWorkflow(CollectionResponse sequenceFilesCollection,
				Dataset referenceDataset, History workflowHistory) {
			this.sequenceFilesCollection = sequenceFilesCollection;
			this.referenceDataset = referenceDataset;
			this.workflowHistory = workflowHistory;
		}

		public CollectionResponse getSequenceFilesCollection() {
			return sequenceFilesCollection;
		}

		public Dataset getReferenceDataset() {
			return referenceDataset;
		}

		public History getWorkflowHistory() {
			return workflowHistory;
		}
	}
	
	public GalaxyWorkflowPreparationServicePhylogenomicsPipeline(GalaxyHistoriesService galaxyHistoriesService) {
		this.galaxyHistoriesService = galaxyHistoriesService;
	}
	
	public GalaxyPreparedWorkflow prepareWorkflow(AnalysisSubmissionGalaxyPhylogenomicsPipeline analysisSubmission) throws ExecutionManagerException {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		
		Set<SequenceFile> sequenceFiles = analysisSubmission.getInputFiles();
		List<Path> sequenceFilePaths = new LinkedList<>();
		for (SequenceFile file : sequenceFiles) {
			sequenceFilePaths.add(file.getFile());
		}
		
		ReferenceFile referenceFile = analysisSubmission.getReferenceFile();
		History workflowHistory = galaxyHistoriesService.newHistoryForWorkflow();
		
		List<Dataset> sequenceDatasets = galaxyHistoriesService.
				uploadFilesListToHistory(sequenceFilePaths, InputFileType.FASTQ_SANGER, workflowHistory);
		
		Dataset referenceDataset = galaxyHistoriesService.
				fileToHistory(referenceFile.getFile(), InputFileType.FASTQ_SANGER, workflowHistory);
		
		CollectionResponse collectionResponse = 
				galaxyHistoriesService.constructCollectionList(sequenceDatasets, workflowHistory);

		return new GalaxyPreparedWorkflow(collectionResponse, referenceDataset, workflowHistory);
	}
}
