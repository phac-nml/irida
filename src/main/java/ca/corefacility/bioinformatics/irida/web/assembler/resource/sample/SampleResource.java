package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.sample.Host;
import ca.corefacility.bioinformatics.irida.model.sample.Host.Sex;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A resource for {@link Sample}s.
 *
 */
@XmlRootElement(name = "sample")
public class SampleResource extends IdentifiableResource<Sample> {

	private int sequenceFileCount;

	public SampleResource() {
		super(new Sample());
	}

	@XmlElement
	public String getSampleName() {
		return resource.getSampleName();
	}

	@JsonProperty
	public void setSampleName(String sampleName) {
		resource.setSampleName(sampleName);
	}

	@XmlElement
	public String getSequencerSampleId() {
		return resource.getSequencerSampleId();
	}

	public void setSequencerSampleId(String sampleId) {
		resource.setSequencerSampleId(sampleId);
	}

	@XmlElement
	public String getDescription() {
		return resource.getDescription();
	}

	public void setDescription(String description) {
		resource.setDescription(description);
	}

	@XmlElement
	public String getOrganism() {
		return resource.getOrganism();
	}

	public void setOrganism(String organism) {
		resource.setOrganism(organism);
	}

	@XmlElement
	public String getIsolate() {
		return resource.getIsolate();
	}

	public void setIsolate(String isolate) {
		resource.setIsolate(isolate);
	}

	@XmlElement
	public String getStrain() {
		return resource.getStrain();
	}

	public void setStrain(String strain) {
		resource.setStrain(strain);
	}

	@XmlElement
	public String getCollectedBy() {
		return resource.getCollectedBy();
	}

	public void setCollectedBy(String collectedBy) {
		resource.setCollectedBy(collectedBy);
	}

	@XmlElement
	public Date getCollectionDate() {
		return resource.getCollectionDate();
	}

	public void setCollectionDate(Date collectionDate) {
		resource.setCollectionDate(collectionDate);
	}

	@XmlElement
	public String getGeographicLocationName() {
		return resource.getGeographicLocationName();
	}

	public void setGeographicLocationName(String geographicLocationName) {
		resource.setGeographicLocationName(geographicLocationName);
	}

	@XmlElement
	public String getIsolationSource() {
		return resource.getIsolationSource();
	}

	public void setIsolationSource(String isolationSource) {
		resource.setIsolationSource(isolationSource);
	}

	@XmlElement
	public String getLatitude() {
		return resource.getLatitude();
	}

	public void setLatitude(String latitude) {
		resource.setLatitude(latitude);
	}

	@XmlElement
	public String getLongitude() {
		return resource.getLongitude();
	}

	public void setLongitude(String longitude) {
		resource.setLongitude(longitude);
	}

	@XmlElement
	public String getCultureCollection() {
		return resource.getCultureCollection();
	}

	public void setCultureCollection(String cultureCollection) {
		resource.setCultureCollection(cultureCollection);
	}

	@XmlElement
	public String getGenotype() {
		return resource.getGenotype();
	}

	public void setGenotype(String genotype) {
		resource.setGenotype(genotype);
	}

	@XmlElement
	public String getPassageHistory() {
		return resource.getPassageHistory();
	}

	public void setPassageHistory(String passageHistory) {
		resource.setPassageHistory(passageHistory);
	}

	@XmlElement
	public String getPathotype() {
		return resource.getPathotype();
	}

	public void setPathotype(String pathotype) {
		resource.setPathotype(pathotype);
	}

	@XmlElement
	public String getSerotype() {
		return resource.getSerotype();
	}

	public void setSerotype(String serotype) {
		resource.setSerotype(serotype);
	}

	@XmlElement
	public String getSerovar() {
		return resource.getSerovar();
	}

	public void setSerovar(String serovar) {
		resource.setSerovar(serovar);
	}

	@XmlElement
	public String getSpecimenVoucher() {
		return resource.getSpecimenVoucher();
	}

	public void setSpecimenVoucher(String specimenVoucher) {
		resource.setSpecimenVoucher(specimenVoucher);
	}

	@XmlElement
	public String getSubtype() {
		return resource.getSubtype();
	}

	public void setSubtype(String subtype) {
		resource.setSubtype(subtype);
	}

	@XmlElement
	public String getHostTaxonomicName() {
		if (resource.getHost() != null) {
			return resource.getHost().getTaxonomicName();
		} else {
			return null;
		}
	}

	public void setHostTaxonomicName(String taxonomicName) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setTaxonomicName(taxonomicName);
	}

	@XmlElement
	public String getHostDisease() {
		if (resource.getHost() != null) {
			return resource.getHost().getDisease();
		} else {
			return null;
		}
	}

	public void setHostDisease(String disease) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setDisease(disease);
	}

	@XmlElement
	public String getHostDescription() {
		if (resource.getHost() != null) {
			return resource.getHost().getDescription();
		} else {
			return null;
		}
	}

	public void setHostDescription(String description) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setDescription(description);
	}

	@XmlElement
	public String getHostDiseaseOutcome() {
		if (resource.getHost() != null) {
			return resource.getHost().getDiseaseOutcome();
		} else {
			return null;
		}
	}

	public void setHostDiseaseOutcome(String diseaseOutcome) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setDiseaseOutcome(diseaseOutcome);
	}

	@XmlElement
	public String getHostDiseaseStage() {
		if (resource.getHost() != null) {
			return resource.getHost().getDiseaseStage();
		} else {
			return null;
		}
	}

	public void setHostDiseaseStage(String diseaseStage) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setDiseaseStage(diseaseStage);
	}

	@XmlElement
	public String getHostHealthState() {
		if (resource.getHost() != null) {
			return resource.getHost().getHealthState();
		} else {
			return null;
		}
	}

	public void setHostHealthState(String healthState) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setHealthState(healthState);
	}

	@XmlElement
	public Sex getHostSex() {
		if (resource.getHost() != null) {
			return resource.getHost().getSex();
		} else {
			return null;
		}
	}

	public void setHostSex(Sex sex) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setSex(sex);
	}

	@XmlElement
	public String getHostSubjectId() {
		if (resource.getHost() != null) {
			return resource.getHost().getSubjectId();
		} else {
			return null;
		}
	}

	public void setHostSubjectId(String subjectId) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setSubjectId(subjectId);
	}

	@XmlElement
	public String getHostTissueSampleId() {
		if (resource.getHost() != null) {
			return resource.getHost().getTissueSampleId();
		} else {
			return null;
		}
	}

	public void setHostTissueSampleId(String tissueSampleId) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setTissueSampleId(tissueSampleId);
	}

	@XmlElement
	public Integer getHostAge() {
		if (resource.getHost() != null) {
			return resource.getHost().getAge();
		} else {
			return null;
		}
	}

	public void setHostAge(Integer age) {
		if (resource.getHost() == null) {
			resource.setHost(new Host());
		}
		resource.getHost().setAge(age);
	}

	public void setSequenceFileCount(int sequenceFileCount) {
		this.sequenceFileCount = sequenceFileCount;
	}

	@XmlElement
	public int getSequenceFileCount() {
		return sequenceFileCount;
	}
}