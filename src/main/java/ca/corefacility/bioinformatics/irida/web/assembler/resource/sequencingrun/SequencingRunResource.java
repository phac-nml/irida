package ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencingrun;

import javax.xml.bind.annotation.XmlElement;

import ca.corefacility.bioinformatics.irida.model.enums.UploadStatus;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

/**
 * Resource class for a {@link SequencingRun}. This class will be extended by
 * individual sequencing run resource types
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public abstract class SequencingRunResource extends IdentifiableResource<SequencingRun> {

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

	public UploadStatus getUploadStatus() {
		return resource.getUploadStatus();
	}

	public void setUploadStatus(UploadStatus uploadStatus) {
		resource.setUploadStatus(uploadStatus);
	}

}
