package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import java.util.List;


/**
 * Used to return {@link Sample} metadata back to the user interface.
 */
public class SampleMetadata {
	private List<SampleMetadataFieldEntry> metadata;

	public SampleMetadata(List<SampleMetadataFieldEntry> metadata) {
		this.metadata = metadata;
	}

	public List<SampleMetadataFieldEntry> getMetadata() {
		return metadata;
	}


}
