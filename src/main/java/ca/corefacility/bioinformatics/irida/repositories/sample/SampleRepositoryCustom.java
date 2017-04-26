package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Custom repository interface for {@link SampleRepository}
 */
public interface SampleRepositoryCustom {
	/**
	 * Get all {@link Sample} metadata keys in the database for a given query
	 * 
	 * @param query
	 *            the query string
	 * @return a list of metadata keys
	 */
	public List<String> getSampleMetadataKeys(String query);
}
