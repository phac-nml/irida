package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import java.nio.file.Path;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.SequenceFileSnapshotService;

/**
 * {@link CRUDService} implementation of {@link SequenceFileSnapshotService}
 */
@Service
@PreAuthorize("permitAll")
public class SequenceFileSnapshotServiceImpl extends CRUDServiceImpl<Long, SequenceFileSnapshot> implements
		SequenceFileSnapshotService {

	private static final String FILE_PROPERTY = "file";

	SequenceFileRemoteRepository remoteRepository;
	RemoteAPIRepository remoteApiRepo;

	@Autowired
	public SequenceFileSnapshotServiceImpl(SequenceFileSnapshotRepository repository,
			SequenceFileRemoteRepository remoteRepository, RemoteAPIRepository remoteApiRepo, Validator validator) {
		super(repository, validator, SequenceFileSnapshot.class);
		this.remoteRepository = remoteRepository;
		this.remoteApiRepo = remoteApiRepo;
	}

	/**
	 * {@inheritDoc}
	 */
	public SequenceFileSnapshot mirrorFile(SequenceFile file) {

		SequenceFileSnapshot mirror = new SequenceFileSnapshot(file);

		return create(mirror);
	}

	@Override
	public SequenceFileSnapshot mirrorFileContent(SequenceFileSnapshot snapshot) {
		RemoteAPI remoteAPIForUrl = remoteApiRepo.getRemoteAPIForUrl(snapshot.getRemoteURI());

		Path downloadRemoteSequenceFile = remoteRepository.downloadRemoteSequenceFile(snapshot.getRemoteURI(),
				remoteAPIForUrl);

		return update(snapshot.getId(), ImmutableMap.of(FILE_PROPERTY, downloadRemoteSequenceFile));
	}

}
