package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
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

    /**
     * Get a {@link SequenceFile} that is associated with a specific {@Link Project}. If the {@link SequenceFile} is
     * not associated with the {@link Project}, or no {@link SequenceFile} with the specified {@link Identifier} exists,
     * then an {@link ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException} will be thrown.
     *
     * @param project        the {@link Project} to get the {@link SequenceFile} from.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile} belonging to the {@link Identifier}.
     * @throws EntityNotFoundException when the {@link Identifier} specified does not exist, or is not associated with
     *                                 the supplied {@link Project}.
     */
    public SequenceFile getSequenceFileFromProject(Project project, Identifier sequenceFileId) throws EntityNotFoundException;
}
