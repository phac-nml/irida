package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;
import java.util.UUID;

public class UIPipelineDetailsResponse {
	private UUID id;
	private String name;
	private boolean canPipelineWriteToSamples;
	private boolean requiresReference;
	private List<UIReferenceFile> files;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<UIReferenceFile> getFiles() {
			return files;
		}

	public void setFiles(List<UIReferenceFile> files) {
		this.files = files;
	}

	public boolean isCanPipelineWriteToSamples() {
		return canPipelineWriteToSamples;
	}

	/*
	This will let us know if the pipeline is able to write back information to the samples.  If this is possible
	then we should display a checkbox in the UI asking the user if they want this to occur or not.  User might
	not want it to happen if they are re-running an analysis or just don't want it written back into the sample
	metadata.
	 */
	public void setCanPipelineWriteToSamples(boolean canPipelineWriteToSamples) {
		this.canPipelineWriteToSamples = canPipelineWriteToSamples;
	}

	public void setRequiresReference(boolean requiresReference) {
		this.requiresReference = requiresReference;
	}

	public boolean isRequiresReference() {
		return requiresReference;
	}
}
