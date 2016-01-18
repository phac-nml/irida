package ca.corefacility.bioinformatics.irida.service.impl.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;

/**
 * {@link SequenceFile} processor which launches a {@link FileProcessingChain}
 * on a collection of {@link SequenceFile} ids
 */
public class SequenceFileProcessorLauncher implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileProcessorLauncher.class);

	private final FileProcessingChain fileProcessingChain;
	private final Long sequencingObjectId;
	private final SecurityContext securityContext;

	public SequenceFileProcessorLauncher(FileProcessingChain fileProcessingChain, Long sequencingObjectId,
			SecurityContext securityContext) {
		this.fileProcessingChain = fileProcessingChain;
		this.sequencingObjectId = sequencingObjectId;
		this.securityContext = securityContext;
	}

	@Override
	public void run() {
		// when running in single-threaded mode, the security context should
		// already be populated in the current thread and and we shouldn't
		// have to overwrite and erase the context before execution.
		boolean copiedSecurityContext = true;
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null || context.getAuthentication() == null) {
			SecurityContextHolder.setContext(securityContext);
		} else {
			copiedSecurityContext = false;
		}

		// proceed with analysis
		try {
			fileProcessingChain.launchChain(sequencingObjectId);
		} catch (FileProcessorTimeoutException e) {
			logger.error(
					"FileProcessingChain did *not* execute -- the transaction opened by SequenceFileService never closed.",
					e);
		}

		// erase the security context if we copied the context into the
		// current thread.
		if (copiedSecurityContext) {
			SecurityContextHolder.clearContext();
		}
	}
}
