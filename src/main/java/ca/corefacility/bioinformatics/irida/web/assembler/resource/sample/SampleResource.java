package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.Sample;
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
    public String getSampleId(){
        return resource.getSampleId();
    }
    public void setSampleId(String sampleId){
        resource.setSampleId(sampleId);
    }
    
    @XmlElement
    public String getSamplePlate() {
        return resource.getSamplePlate();
    }

    public void setSamplePlate(String samplePlate) {
        resource.setSamplePlate(samplePlate);
    }

    @XmlElement
    public String getSampleWell() {
        return resource.getSampleWell();
    }

    public void setSampleWell(String sampleWell) {
        resource.setSampleWell(sampleWell);
    }

    @XmlElement
    public String getI7IndexId() {
        return resource.getI7IndexId();
    }

    public void setI7IndexId(String i7IndexId) {
        resource.setI7IndexId(i7IndexId);
    }

    @XmlElement
    public String getI7Index() {
        return resource.getI7Index();
    }

    public void setI7Index(String i7Index) {
        resource.setI7Index(i7Index);
    }

    @XmlElement
    public String getI5IndexId() {
        return resource.getI5IndexId();
    }

    public void setI5IndexId(String i5IndexId) {
        resource.setI5IndexId(i5IndexId);
    }

    @XmlElement
    public String getI5Index() {
        return resource.getI5Index();
    }

    public void setI5Index(String i5Index) {
        resource.setI5Index(i5Index);
    }

    @XmlElement
    public String getDescription() {
        return resource.getDescription();
    }

    public void setDescription(String description) {
        resource.setDescription(description);
    }
}
