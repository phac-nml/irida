package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequencingObjectRemoteService;

/**
 * Service to read SequencingObjects from a remote api.
 * @param <Type> The type of sequencing object for this service
 */
public class SequencingObjectRemoteServiceImpl<Type extends SequencingObject> extends RemoteServiceImpl<Type>
		implements SequencingObjectRemoteService<Type> {
	public SequenceFileRemoteRepository sequenceFileRemoteRepository;

	public SequencingObjectRemoteServiceImpl(RemoteRepository<Type> repository,
			SequenceFileRemoteRepository sequenceFileRemoteRepository, RemoteAPIRepository remoteAPIRepository) {
		super(repository, remoteAPIRepository);
		this.sequenceFileRemoteRepository = sequenceFileRemoteRepository;
	}

	@Override
	public Type mirrorSequencingObject(Type seqObject) {

		Set<SequenceFile> files = seqObject.getFiles();

		for (SequenceFile file : files) {
			String fileHref = file.getSelfHref();
			RemoteAPI api = getRemoteApiForURI(fileHref);
			Path downloadRemoteSequenceFile = sequenceFileRemoteRepository.downloadRemoteSequenceFile(fileHref, api);
			file.setFile(downloadRemoteSequenceFile);
		}

		return seqObject;
	}
}
