package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.sample.Host.Sex;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A resource for {@link Sample}s.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "sample")
public class SampleResource extends IdentifiableResource<Sample> {

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
	public String getExternalSampleId() {
		return resource.getSequencerSampleId();
	}

	public void setExternalSampleId(String sampleId) {
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
		return resource.getHost().getTaxonomicName();
	}

	public void setHostTaxonomicName(String taxonomicName) {
		resource.getHost().setTaxonomicName(taxonomicName);
	}

	@XmlElement
	public String getHostDisease() {
		return resource.getHost().getDisease();
	}

	public void setHostDisease(String disease) {
		resource.getHost().setDisease(disease);
	}

	@XmlElement
	public String getHostDescription() {
		return resource.getHost().getDescription();
	}

	public void setHostDescription(String description) {
		resource.getHost().setDescription(description);
	}

	@XmlElement
	public String getHostDiseaseOutcome() {
		return resource.getHost().getDiseaseOutcome();
	}

	public void setHostDiseaseOutcome(String diseaseOutcome) {
		resource.getHost().setDiseaseOutcome(diseaseOutcome);
	}

	@XmlElement
	public String getHostDiseaseStage() {
		return resource.getHost().getDiseaseStage();
	}

	public void setHostDiseaseStage(String diseaseStage) {
		resource.getHost().setDiseaseStage(diseaseStage);
	}

	@XmlElement
	public String getHostHealthState() {
		return resource.getHost().getHealthState();
	}

	public void setHostHealthState(String healthState) {
		resource.getHost().setHealthState(healthState);
	}

	@XmlElement
	public Sex getHostSex() {
		return resource.getHost().getSex();
	}

	public void setHostSex(Sex sex) {
		resource.getHost().setSex(sex);
	}

	@XmlElement
	public String getHostSubjectId() {
		return resource.getHost().getSubjectId();
	}

	public void setHostSubjectId(String subjectId) {
		resource.getHost().setSubjectId(subjectId);
	}
	
	@XmlElement
	public String getHostTissueSampleId() {
		return resource.getHost().getTissueSampleId();
	}
	
	public void setHostTissueSampleId(String tissueSampleId) {
		resource.getHost().setTissueSampleId(tissueSampleId);
	}
	
	@XmlElement
	public Integer getHostAge() {
		return resource.getHost().getAge();
	}
	
	public void setHostAge(Integer age) {
		resource.getHost().setAge(age);
	}
}
