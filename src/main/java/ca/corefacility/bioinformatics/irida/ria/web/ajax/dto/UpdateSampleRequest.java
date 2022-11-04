package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

/**
 * UI Request to update an existing sample
 */
public class UpdateSampleRequest extends CreateSampleRequest {
	private Long sampleId;

	public UpdateSampleRequest(Long sampleID, String name, String organism, String description,
			List<FieldUpdate> metadata) {
		super(name, organism, description, metadata);
		this.sampleId = sampleID;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
}
