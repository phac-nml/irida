package ca.corefacility.bioinformatics.irida.service.impl;

import java.nio.file.Path;
import java.util.Collection;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import ca.corefacility.bioinformatics.irida.repositories.SnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.SnapshotService;

@Service
public class SnapshotServiceImpl extends CRUDServiceImpl<Long, Snapshot> implements SnapshotService{

	private SequenceFileRemoteRepository sequenceFileRemoteRepository;

	private SnapshotRepository snapshotRepository;

	@Autowired
	public SnapshotServiceImpl(SnapshotRepository snapshotRepository,
			SequenceFileRemoteRepository sequenceFileRemoteRepository, Validator validator) {
		super(snapshotRepository, validator, Snapshot.class);
		this.snapshotRepository = snapshotRepository;
		this.sequenceFileRemoteRepository = sequenceFileRemoteRepository;
	}

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
			return new SequenceFileSnapshot(file.getFile(), file.getOptionalProperties());
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
}
