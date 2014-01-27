package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile;

/**
 * Aspect that handles post-processing of {@link SequenceFile} after a
 * transaction has been committed.
 * 
 * This aspect watches for methods annotated with {@link ModifiesSequenceFile}
 * annotations.
 * 
 * @see ModifiesSequenceFile
 * 
 *      This aspect should be executed *after* a transaction has been committed.
 *      Note that this aspect is annotated with an {@link Order} that is lower
 *      than the {@link Order} specified by the transaction manager. See:
 *      http://
 *      www.coderanch.com/t/607422/Spring/Spring-Aspects-advices-transactions
 *      and http://forum.springsource.org/showthread.php?85082-Aspect-Order for
 *      more information.
 * 
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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

	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile)", returning = "sequenceFileJoin")
	public void postProcess(JoinPoint jp, Join<Sample, SequenceFile> sequenceFileJoin) {
		logger.debug("Executing afterReturning advice for creating a new sequence file in a sample.");
		executeProcessor(sequenceFileJoin.getObject());
	}

	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile) && args(id, updatedFields)", returning = "sequenceFile")
	public void postProcess(JoinPoint jp, SequenceFile sequenceFile, Long id, Map<String, Object> updatedFields) {
		logger.debug("Executing afterReturning advice for updating a sequence file.");

		if (updatedFields.containsKey("file")) {
			executeProcessor(sequenceFile);
		}
	}

	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile)", returning = "sequenceFile")
	public void postProcess(JoinPoint jp, SequenceFile sequenceFile) {
		logger.debug("Executing afterReturning advice for creating a sequence file.");

		executeProcessor(sequenceFile);
	}

	private void executeProcessor(SequenceFile sequenceFile) {
		taskExecutor.execute(new SequenceFileProcessorLauncher(fileProcessingChain, sequenceFile, SecurityContextHolder
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
			fileProcessingChain.launchChain(sequenceFile);

			// erase the security context if we copied the context into the
			// current thread.
			if (copiedSecurityContext) {
				SecurityContextHolder.clearContext();
			}
		}

	}
}
