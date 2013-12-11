package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.any;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FileProcessorAspect;

import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link FileProcessorAspect}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class FileProcessorAspectTest {
	private JoinPoint joinPoint;
	private FileProcessingChain fileProcessingChain;
	private TaskExecutor taskExecutor;
	private FileProcessorAspect fileProcessorAspect;

	@Before
	public void setUp() {
		joinPoint = mock(JoinPoint.class);
		fileProcessingChain = mock(FileProcessingChain.class);
		taskExecutor = mock(TaskExecutor.class);
		fileProcessorAspect = new FileProcessorAspect(fileProcessingChain, taskExecutor);
	}

	@Test
	public void testUpdateProcessFile() {
		SequenceFile sequenceFile = new SequenceFile();

		fileProcessorAspect.postProcess(joinPoint, sequenceFile, 1L, ImmutableMap.of("file", new Object()));

		verify(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	public void testUpdateNoProcessFile() {
		SequenceFile sequenceFile = new SequenceFile();

		fileProcessorAspect.postProcess(joinPoint, sequenceFile, 1L, ImmutableMap.of("notfile", new Object()));

		verifyZeroInteractions(taskExecutor);
	}
}
