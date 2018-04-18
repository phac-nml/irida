package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.impl.processor.SequenceFileProcessorLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("singleton")
public class SequencingObjectProcessingService {
	private SequencingObjectRepository sequencingObjectRepository;

	private FileProcessingChain fileProcessingChain;
	private TaskExecutor fileProcessingChainExecutor;

	@Autowired
	public SequencingObjectProcessingService(SequencingObjectRepository sequencingObjectRepository,
			@Qualifier("fileProcessingChainExecutor") TaskExecutor executor,
			@Qualifier("uploadFileProcessingChain") FileProcessingChain fileProcessingChain) {
		this.sequencingObjectRepository = sequencingObjectRepository;
		this.fileProcessingChain = fileProcessingChain;
		this.fileProcessingChainExecutor = executor;
	}

	public synchronized void findFilesToProcess() {
		List<SequencingObject> toProcess = sequencingObjectRepository
				.getSequencingObjectsWithProcessingState(SequencingObject.ProcessingState.UNPROCESSED);

		for (SequencingObject process : toProcess) {
			process.setProcessingState(SequencingObject.ProcessingState.QUEUED);

			sequencingObjectRepository.save(process);
		}

		for (SequencingObject process : toProcess) {
			fileProcessingChainExecutor.execute(new SequenceFileProcessorLauncher(fileProcessingChain, process.getId(),
					SecurityContextHolder.getContext()));
		}

	}
}
