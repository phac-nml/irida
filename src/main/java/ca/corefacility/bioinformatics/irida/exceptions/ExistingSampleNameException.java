package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Exception thrown when trying to add a {@link ca.corefacility.bioinformatics.irida.model.sample.Sample} to a {@link ca.corefacility.bioinformatics.irida.model.project.Project} with a duplicate sample name
 */
public class ExistingSampleNameException extends EntityExistsException {
	Sample sample;

	public ExistingSampleNameException(String message) {
		super(message);
	}

	public ExistingSampleNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExistingSampleNameException(String message, Sample sample) {
		super(message);
		this.sample = sample;
	}

	public Sample getSample() {
		return sample;
	}
}
