package ca.corefacility.bioinformatics.irida.processing.impl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;

/**
 * Aspect that handles post-processing of {@link SequenceFile} after a
 * transaction has been committed.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Aspect
@Order(500)
public class FileProcessorAspect {
	private static final Logger logger = LoggerFactory.getLogger(FileProcessorAspect.class);
	private final FileProcessingChain fileProcessingChain;
	private final TaskExecutor taskExecutor;

	public FileProcessorAspect(FileProcessingChain fileProcessingChain, TaskExecutor taskExecutor) {
		this.fileProcessingChain = fileProcessingChain;
		this.taskExecutor = taskExecutor;
	}

	@AfterReturning(value = "execution(* ca.corefacility.bioinformatics.irida.service..*SequenceFileService*.*(..))")
	public void postProcess(JoinPoint jp) {
		SequenceFile sf = null;
		logger.debug("JoinPoint [" + jp.toString() + "]");
		logger.debug("\tTarget [" + jp.getTarget().toString() + "]");
		logger.debug("\tArgs [" + jp.getArgs() + "]");
		logger.debug("\tThis [" + jp.getThis() + "]");
		// logger.debug("SequenceFile [" + sequenceFile + "]");
		for (Object o : jp.getArgs()) {
			if (o instanceof SequenceFile) {
				sf = (SequenceFile) o;
				break;
			}
		}

		taskExecutor.execute(new SequenceFileProcessorLauncher(fileProcessingChain, sf, SecurityContextHolder
				.getContext()));
	}

	/**
	 * Executes {@link FileProcessingChain} asynchronously in a
	 * {@link TaskExecutor}.
	 * 
	 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
	 * 
	 */
	private static final class SequenceFileProcessorLauncher implements Runnable {
		private final FileProcessingChain fileProcessingChain;
		private final SequenceFile sequenceFile;
		private final SecurityContext securityContext;

		public SequenceFileProcessorLauncher(FileProcessingChain fileProcessingChain, SequenceFile sequenceFile,
				SecurityContext securityContext) {
			this.fileProcessingChain = fileProcessingChain;
			this.sequenceFile = sequenceFile;
			this.securityContext = securityContext;
		}

		@Override
		public void run() {
			logger.debug("Inside thread: " + Thread.currentThread().toString());
			SecurityContextHolder.setContext(securityContext);
			fileProcessingChain.launchChain(sequenceFile);
			SecurityContextHolder.clearContext();
		}

	}
}
