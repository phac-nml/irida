package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

//ISS
import ca.corefacility.bioinformatics.irida.model.project.Project;
import java.util.List;

/**
 * Custom repository methods for {@link Sample}s
 */
public interface SampleRepositoryCustom {

	/**
	 * Update the modifiedDate in a {@link Sample} to the specified value.
	 *
	 * @param sample       The {@link Sample} to update
	 * @param modifiedDate The new {@link Date}
	 */
	void updateSampleModifiedDate(Sample sample, Date modifiedDate);

    //ISS---
	public List<Long> getSampleIdsByCodeInProject(Project project, List<String> sampleCodes);
	public String getClusterIdByCodes(Project project, List<String> sampleCodes);
	public Long getMasterProjectIdByCode(String sampleCode);
	public void setClusterIdByCode(Project project, List<String> sampleCodes, String clusterId);
	public String getNextClusterId(Project project);
	public List<String> getRecipientsByCodes(Project project, List<String> sampleCodes, Boolean isAlert);
	public List<Sample> getSamplesForClusterShallow(Project project, String sampleCode, String clusterId);
	//---ISS
}
