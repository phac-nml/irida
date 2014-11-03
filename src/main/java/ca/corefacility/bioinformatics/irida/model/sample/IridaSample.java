package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;

public interface IridaSample {
	public Long getId();

	public String getSampleName();

	public String getSequencerSampleId();

	public String getDescription();

	public String getStrain();

	public Date getCollectionDate();

	public String getCollectedBy();

	public String getLatitude();

	public String getLongitude();

	public String getOrganism();

	public String getIsolate();

	public String getGeographicLocationName();

	public String getIsolationSource();

	public String getCultureCollection();

	public String getGenotype();

	public String getPassageHistory();

	public String getPathotype();

	public String getSerotype();

	public String getSerovar();

	public String getSpecimenVoucher();

	public String getSubgroup();

	public String getSubtype();

}
