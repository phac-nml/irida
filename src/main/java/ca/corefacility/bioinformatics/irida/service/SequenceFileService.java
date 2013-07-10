package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

/**
 * Service for managing {@link SequenceFile} entities.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileService extends CRUDService<Long, SequenceFile> {
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
    public Relationship createSequenceFileWithOwner(SequenceFile sequenceFile, Class ownerType, Long owner);

    /**
     * Get a {@link SequenceFile} that is associated with a specific {@Link Project}. If the {@link SequenceFile} is
     * not associated with the {@link Project}, or no {@link SequenceFile} with the specified {@link Identifier} exists,
     * then an {@link EntityNotFoundException} will be thrown.
     *
     * @param project        the {@link Project} to get the {@link SequenceFile} from.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile} belonging to the {@link Identifier}.
     * @throws EntityNotFoundException when the {@link Identifier} specified does not exist, or is not associated with
     *                                 the supplied {@link Project}.
     */
    public SequenceFile getSequenceFileFromProject(Project project, Long sequenceFileId) throws EntityNotFoundException;

    /**
     * Get a {@link SequenceFile} that is associated with a specific {@link Sample}. If the {@link SequenceFile} is not
     * associated with the {@link Sample}, or no {@link SequenceFile} with the specified {@link Identifier} exists, then
     * an {@link EntityNotFoundException} will be thrown.
     *
     * @param project        the {@link Project} to get the {@link Sample} from.
     * @param sample         the {@link Sample} to get the {@link SequenceFile} from.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile} belonging to the {@link Identifier}.
     * @throws EntityNotFoundException when the {@link Identifier} specified does not exist, or is not associated with
     *                                 the supplied {@link Sample}.
     */
    public SequenceFile getSequenceFileFromSample(Project project, Sample sample, Long sequenceFileId) throws EntityNotFoundException;
}
