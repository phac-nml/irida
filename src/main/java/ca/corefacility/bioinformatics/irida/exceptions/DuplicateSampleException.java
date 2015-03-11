package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;

/**
 * If there is more than one {@link SequenceFile} from a {@link Sample} for a {@link IridaWorkflow}.
 *
 */
public class DuplicateSampleException extends RuntimeException {
	private static final long serialVersionUID = -432490135272603887L;
	public DuplicateSampleException(String message) {
		super(message);
	}
}
