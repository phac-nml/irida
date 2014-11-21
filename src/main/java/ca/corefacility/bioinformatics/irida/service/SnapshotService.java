package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;

import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.irida.IridaProject;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSample;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.snapshot.Snapshot;

/**
 * Service for storing and creating {@link Snapshot}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SnapshotService extends CRUDService<Long, Snapshot> {

	/**
	 * To ensure copies of all of the snapshot objects have been created, use
	 * {@link SnapshotService#takeSnapshot(Collection, Collection, Collection)}
	 * instead
	 */
	@Override
	public Snapshot create(Snapshot object) throws EntityExistsException, ConstraintViolationException,
			UnsupportedOperationException;

	/**
	 * Take a snapshot of collections of {@link IridaProject}s,
	 * {@link IridaSample}s, and {@link IridaSequenceFile}s
	 * 
	 * @param projects
	 *            The {@link IridaProject}s to take a snapshot of
	 * @param samples
	 *            The {@link IridaSample}s to take a snapshot of
	 * @param sequenceFiles
	 *            The {@link IridaSequenceFile}s to take a snapshot of
	 * @return a persisted {@link Snapshot} object
	 */
	public Snapshot takeSnapshot(Collection<? extends IridaProject> projects,
			Collection<? extends IridaSample> samples, Collection<? extends IridaSequenceFile> sequenceFiles);
}
