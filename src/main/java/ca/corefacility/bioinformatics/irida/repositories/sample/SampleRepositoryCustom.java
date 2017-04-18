package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

public interface SampleRepositoryCustom {
	public List<String> getSampleMetadataKeys(String query);
}
