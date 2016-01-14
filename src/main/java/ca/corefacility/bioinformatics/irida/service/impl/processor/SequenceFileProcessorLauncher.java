package ca.corefacility.bioinformatics.irida.service.impl.processor;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;

public class SequenceFileProcessorLauncher implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileProcessorLauncher.class);

	private final FileProcessingChain fileProcessingChain;
	private final Collection<Long> sequenceFileIds;
	private final SecurityContext securityContext;

	public SequenceFileProcessorLauncher(FileProcessingChain fileProcessingChain, Collection<Long> sequenceFileIds,
			SecurityContext securityContext) {
		this.fileProcessingChain = fileProcessingChain;
		this.sequenceFileIds = sequenceFileIds;
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
			for (Long id : sequenceFileIds) {
				fileProcessingChain.launchChain(id);
			}
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
