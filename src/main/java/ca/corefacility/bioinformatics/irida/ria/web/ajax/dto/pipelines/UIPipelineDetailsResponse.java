package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

import java.util.List;
import java.util.UUID;

public class UIPipelineDetailsResponse {
		private UUID id;
		private String name;
		private boolean canPipelineWriteToSamples;
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

		public void setCanPipelineWriteToSamples(boolean canPipelineWriteToSamples) {
			this.canPipelineWriteToSamples = canPipelineWriteToSamples;
		}
}
