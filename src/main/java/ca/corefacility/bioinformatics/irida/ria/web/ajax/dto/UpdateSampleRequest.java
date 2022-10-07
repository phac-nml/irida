package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

/**
 * UI Request to update an existing sample
 */
public class UpdateSampleRequest extends CreateSampleRequest {
	public UpdateSampleRequest(String name, String organism, String description, List<FieldUpdate> metadata) {
		super(name, organism, description, metadata);
	}
}
