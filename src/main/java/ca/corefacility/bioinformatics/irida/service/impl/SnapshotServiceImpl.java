package ca.corefacility.bioinformatics.irida.service.impl;

import java.nio.file.Path;
import java.util.Collection;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.irida.IridaProject;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSample;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.snapshot.Snapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.SampleSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.remote.RemoteProjectSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.remote.RemoteSampleSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.remote.RemoteSequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.SnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.SnapshotService;

/**
 * Implementation of {@link SnapshotService}
 * 
 *
 */
@Service
public class SnapshotServiceImpl extends CRUDServiceImpl<Long, Snapshot> implements SnapshotService {

	private final SequenceFileRemoteRepository sequenceFileRemoteRepository;

	private final SnapshotRepository snapshotRepository;

	private final UserRepository userRepository;

	@Autowired
	public SnapshotServiceImpl(SnapshotRepository snapshotRepository,
			SequenceFileRemoteRepository sequenceFileRemoteRepository, UserRepository userRepository,
			Validator validator) {
		super(snapshotRepository, validator, Snapshot.class);
		this.snapshotRepository = snapshotRepository;
		this.sequenceFileRemoteRepository = sequenceFileRemoteRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Throw an {@link UnsupportedOperationException} and instruct developer to
	 * call
	 * {@link SnapshotService#takeSnapshot(Collection, Collection, Collection)}
	 */
	@Override
	public Snapshot create(Snapshot object) throws ConstraintViolationException, EntityExistsException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Snapshots should be taken with the SnapshotService#takeSnapshot method to ensure local copies of all elements are created.");
	}

	/**
	 * {@inheritDoc} TODO: Change this to use an AnalysisSubmission once it is
	 * fully fleshed out
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projects, 'canReadProject')")
	public Snapshot takeSnapshot(Collection<? extends IridaProject> projects,
			Collection<? extends IridaSample> samples, Collection<? extends IridaSequenceFile> sequenceFiles) {
		Snapshot analysisSnapshot = new Snapshot();

		for (IridaProject p : projects) {
			ProjectSnapshot takeProjectSnapshot = takeProjectSnapshot(p);
			analysisSnapshot.addProject(takeProjectSnapshot);
		}

		for (IridaSample s : samples) {
			SampleSnapshot takeSampleSnapshot = takeSampleSnapshot(s);
			analysisSnapshot.addSample(takeSampleSnapshot);
		}

		for (IridaSequenceFile f : sequenceFiles) {
			SequenceFileSnapshot takeSequenceFileSnapshot = takeSequenceFileSnapshot(f);
			analysisSnapshot.addSequenceFile(takeSequenceFileSnapshot);
		}

		analysisSnapshot.setCreatedBy(getLoggedInUser());

		return snapshotRepository.save(analysisSnapshot);
	}

	/**
	 * Get a snapshot object for an {@link IridaSequenceFile}. If the file is a
	 * {@link RemoteSequenceFile} it will download the file locally
	 * 
	 * @param file
	 *            The {@link IridaSequenceFile}
	 * @return A {@link SequenceFileSnapshot}
	 */
	private SequenceFileSnapshot takeSequenceFileSnapshot(IridaSequenceFile file) {
		if (file instanceof RemoteSequenceFile) {
			RemoteSequenceFile rfile = (RemoteSequenceFile) file;
			Path downloadSequenceFile = sequenceFileRemoteRepository.downloadRemoteSequenceFile(rfile,
					rfile.getRemoteAPI());

			return new RemoteSequenceFileSnapshot(rfile, downloadSequenceFile);
		} else {
			return new SequenceFileSnapshot(file);
		}
	}

	/**
	 * Get a snapshot of an {@link IridaProject}
	 * 
	 * @param project
	 *            The {@link IridaProject} to snapshot
	 * @return a {@link ProjectSnapshot}
	 */
	private ProjectSnapshot takeProjectSnapshot(IridaProject project) {
		if (project instanceof RemoteProject) {
			return new RemoteProjectSnapshot((RemoteProject) project);
		} else {
			return new ProjectSnapshot(project);
		}
	}

	/**
	 * Get a snapshot of an {@link IridaSample}
	 * 
	 * @param sample
	 *            The {@link IridaSample} to snapshot
	 * @return a {@link SampleSnapshot}
	 */
	private SampleSnapshot takeSampleSnapshot(IridaSample sample) {
		if (sample instanceof RemoteSample) {
			return new RemoteSampleSnapshot((RemoteSample) sample);
		} else {
			return new SampleSnapshot(sample);
		}
	}

	/**
	 * Get the currently logged in {@link User} for the snapshot.
	 * 
	 * Note: We attempted to use Spring JPA's <code>@CreatedBy</code> annotation
	 * to fill this in for the snapshot, but it resulted in a massive stack
	 * trace loop. We decided it was easier to implement this ourselves than to
	 * track down the library problem.
	 * 
	 * @return The currently logged in {@link User} from the
	 *         SecurityContextHolder
	 */
	private User getLoggedInUser() {
		UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.loadUserByUsername(principal.getUsername());
	}
}
