package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.impl.processor.SequenceFileProcessorLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;

/**
 * Service used to run a {@link FileProcessingChain} on incoming {@link SequencingObject}s.
 */
@Service
@Scope("singleton")
public class SequencingObjectProcessingService {
	private static final Logger logger = LoggerFactory.getLogger(SequencingObjectProcessingService.class);

	private SequencingObjectRepository sequencingObjectRepository;

	private FileProcessingChain fileProcessingChain;
	private ThreadPoolTaskExecutor fileProcessingChainExecutor;

	private final String machineString;

	@Autowired
	public SequencingObjectProcessingService(SequencingObjectRepository sequencingObjectRepository,
			@Qualifier("fileProcessingChainExecutor") ThreadPoolTaskExecutor executor,
			@Qualifier("uploadFileProcessingChain") FileProcessingChain fileProcessingChain) {
		this.sequencingObjectRepository = sequencingObjectRepository;
		this.fileProcessingChain = fileProcessingChain;
		this.fileProcessingChainExecutor = executor;

		this.machineString = ManagementFactory.getRuntimeMXBean().getName();
	}

	/**
	 * Process new {@link SequencingObject}s uploaded and find new sequences to process next time around
	 */
	@Transactional
	public synchronized void runProcessingJob() {
		processFiles();

		findFilesToProcess();
	}

	/**
	 * Find new {@link SequencingObject}s to process and mark that this process is going to handle them
	 */
	public synchronized void findFilesToProcess() {
		//check our queue space
		int queueSpace = fileProcessingChainExecutor.getCorePoolSize() - fileProcessingChainExecutor.getActiveCount();

		//check for any unprocessed files
		List<SequencingObject> toProcess = sequencingObjectRepository
				.getSequencingObjectsWithProcessingState(SequencingObject.ProcessingState.UNPROCESSED);

		// individually loop through and mark the ones we're going to process.  Looping individually so 2 processes are less likely to write at the same time.
		Iterator<SequencingObject> iterator = toProcess.iterator();
		for (int i = 0; i < queueSpace && iterator.hasNext(); i++) {
			SequencingObject sequencingObject = iterator.next();

			sequencingObjectRepository.markFileProcessor(sequencingObject.getId(), machineString,
					SequencingObject.ProcessingState.QUEUED);
		}
	}

	/**
	 * Process {@link SequencingObject}s that have been locked for processing
	 */
	public synchronized void processFiles() {
		//get sequences previously locked
		List<SequencingObject> toProcess = sequencingObjectRepository
				.getSequencingObjectsWithProcessingStateAndProcessor(SequencingObject.ProcessingState.QUEUED,
						machineString);

		//set their state to PROCESSING and update
		toProcess.stream().forEach(s -> s.setProcessingState(SequencingObject.ProcessingState.PROCESSING));
		sequencingObjectRepository.save(toProcess);

		//launch the file processing chain
		for (SequencingObject sequencingObject : toProcess) {
			fileProcessingChainExecutor.execute(
					new SequenceFileProcessorLauncher(fileProcessingChain, sequencingObject.getId(),
							SecurityContextHolder.getContext()));
		}
	}
}
