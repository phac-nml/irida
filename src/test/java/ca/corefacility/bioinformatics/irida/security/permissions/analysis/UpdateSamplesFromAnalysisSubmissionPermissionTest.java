package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;

/**
 * Tests for {@link UpdateSamplesFromAnalysisSubmissionPermission}
 */
public class UpdateSamplesFromAnalysisSubmissionPermissionTest {

	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Mock
	private UpdateSamplePermission updateSamplePermission;

	@Mock
	private ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission;

	@Mock
	private SampleRepository sampleRepository;

	@Mock
	private UpdateSamplesFromAnalysisSubmissionPermission updateSamplesFromAnalysisSubmissionPermission;

	@Mock
	private Authentication authentication;

	@Mock
	private SequencingObject seq1;

	@Mock
	private SequencingObject seq2;

	private Set<SequencingObject> sequencingObjects;

	private Set<Sample> samples;

	private AnalysisSubmission analysisSubmission;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		updateSamplesFromAnalysisSubmissionPermission = new UpdateSamplesFromAnalysisSubmissionPermission(
				analysisSubmissionRepository, updateSamplePermission, readAnalysisSubmissionPermission,
				sampleRepository);

		sequencingObjects = Sets.newHashSet(seq1, seq2);
		samples = Sets.newHashSet(new Sample(), new Sample());
		analysisSubmission = AnalysisSubmission.builder(UUID.randomUUID()).inputFiles(sequencingObjects).build();
		analysisSubmission.setId(1L);

		when(sampleRepository.findSamplesForAnalysisSubmission(analysisSubmission)).thenReturn(samples);
	}

	@Test
	public void testAllowed() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(true);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(true);

		assertTrue(updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission),
				"Permission allowed");
	}

	@Test
	public void testDeniedReadAnalysisSubmission() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(false);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(true);

		assertFalse(updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission),
				"Permission denied");
	}

	@Test
	public void testDeniedUpdateSamples() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(true);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(false);

		assertFalse(updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission),
				"Permission denied");
	}

	@Test
	public void testDeniedBoth() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(false);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(false);

		assertFalse(updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission),
				"Permission denied");
	}
}
