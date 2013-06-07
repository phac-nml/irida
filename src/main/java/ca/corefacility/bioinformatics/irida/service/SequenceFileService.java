package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

import java.util.Collection;

/**
 * Service for managing {@link SequenceFile} entities.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileService extends CRUDService<Identifier, SequenceFile> {

    /**
     * Get a the collection of {@link SequenceFile} that belong to a particular {@link Sample}.
     *
     * @param s the {@link Sample} to get {@link SequenceFile} entities for.
     * @return the collection of {@link SequenceFile} entities for the {@link Sample}.
     */
    public Collection<SequenceFile> getSequenceFilesForSample(Sample s);

    /**
     * Persist the {@link SequenceFile} to the database and create a new relationship between the {@link SequenceFile}
     * and some other entity identified by the {@link Identifier}.
     *
     * @param sequenceFile the {@link SequenceFile} to be persisted.
     * @param ownerType    the type of the owner.
     * @param owner        the {@link Identifier} of the owning entity.
     * @return the {@link Relationship} between the {@link SequenceFile} and its owner where the subject
     *         {@link Identifier} is that of the owner and the object {@link Identifier} is that of the
     *         {@link SequenceFile}.
     */
    public Relationship createSequenceFileWithOwner(SequenceFile sequenceFile, Class ownerType, Identifier owner);
}
