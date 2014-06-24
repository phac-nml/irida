package ca.corefacility.bioinformatics.irida.web.assembler.resource.miseqrun;

import javax.xml.bind.annotation.XmlElement;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

public class SequencingRunResource extends IdentifiableResource<SequencingRun> {

	public SequencingRunResource(){
		super(null);
	}
	
	public SequencingRunResource(SequencingRun resource) {
		super(resource);
	}

    @XmlElement
    public String getDescription() {
        return resource.getDescription();
    }

    public void setDescription(String description) {
        resource.setDescription(description);
    }

}
