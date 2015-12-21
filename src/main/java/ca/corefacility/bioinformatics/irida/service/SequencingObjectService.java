package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public interface SequencingObjectService extends CRUDService<Long, SequencingObject> {

	public SampleSequencingObjectJoin createSequencingObjectInSample(SequencingObject seqObject, Sample sample);
}
