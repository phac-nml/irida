package ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

public class GalaxyPreparedWorkflowPhylogenomicsPipeline {
	private CollectionResponse sequenceFilesCollection;
	private Dataset referenceDataset;
	private History workflowHistory;
	
	public GalaxyPreparedWorkflowPhylogenomicsPipeline(CollectionResponse sequenceFilesCollection,
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
