
package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import java.util.List;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface OverrepresentedSequenceService extends CRUDService<Long, OverrepresentedSequence> {
	public List<Join<SequenceFile,OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile);
}
