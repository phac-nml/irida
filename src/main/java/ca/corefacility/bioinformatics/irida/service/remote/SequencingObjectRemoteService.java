package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Service for reading {@link SequencingObject}s from remote sources
 * 
 * @param <Type>
 *            an entity extending {@link SequencingObject}
 */
public interface SequencingObjectRemoteService<Type extends SequencingObject> extends RemoteService<Type> {

	/**
	 * Mirror a remote SequencingObject to the local system
	 * 
	 * @param seqObject
	 *            a SequencingObject read from a remote source
	 * @return The locally mirrored {@link SequencingObject}
	 */
	public Type mirrorSequencingObject(Type seqObject);
}
