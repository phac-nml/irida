package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.impl.processor.SequenceFileProcessorLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

		logger.trace("Processor " + machineString + " + has queuespace: " + queueSpace);

		//check for any unprocessed files
		List<SequencingObject> toProcess = sequencingObjectRepository
				.getSequencingObjectsWithProcessingState(SequencingObject.ProcessingState.UNPROCESSED);

		// filter out sequencing objects on a SequencingRun that is not in a COMPLETE state
		toProcess.removeIf(seqObj -> (
				(seqObj.getSequencingRun() != null) &&
				(!seqObj.getSequencingRun().getUploadStatus().equals(SequencingRunUploadStatus.COMPLETE))
		));

		// individually loop through and mark the ones we're going to process.  Looping individually so 2 processes are less likely to write at the same time.
		Iterator<SequencingObject> iterator = toProcess.iterator();

		while (queueSpace > 0 && iterator.hasNext()) {
			SequencingObject sequencingObject = iterator.next();

			logger.trace("File processor " + machineString + " is processing file " + sequencingObject.getId());

			try {
				sequencingObjectRepository.markFileProcessor(sequencingObject.getId(), machineString,
						SequencingObject.ProcessingState.QUEUED);

				queueSpace--;
			} catch (CannotAcquireLockException ex) {
				//If we can't get the lock, another processor is trying to pick up this file.  Let them have it.
				logger.debug("Couldn't get transaction lock to mark file " + sequencingObject.getId());
			}
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
		sequencingObjectRepository.saveAll(toProcess);

		//launch the file processing chain
		for (SequencingObject sequencingObject : toProcess) {
			fileProcessingChainExecutor.execute(
					new SequenceFileProcessorLauncher(fileProcessingChain, sequencingObject.getId(),
							SecurityContextHolder.getContext()));
		}
	}
}
