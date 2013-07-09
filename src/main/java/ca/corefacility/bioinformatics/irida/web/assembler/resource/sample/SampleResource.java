package ca.corefacility.bioinformatics.irida.web.assembler.resource.sample;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A resource for {@link Sample}s.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "sample")
public class SampleResource extends Resource<Identifier, Sample> {

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
}
