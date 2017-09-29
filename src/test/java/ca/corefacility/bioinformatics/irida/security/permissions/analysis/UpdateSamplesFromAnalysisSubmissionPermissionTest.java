package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

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
	private SampleService sampleService;

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

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		updateSamplesFromAnalysisSubmissionPermission = new UpdateSamplesFromAnalysisSubmissionPermission(
				analysisSubmissionRepository, updateSamplePermission, readAnalysisSubmissionPermission, sampleService);

		sequencingObjects = Sets.newHashSet(seq1, seq2);
		samples = Sets.newHashSet(new Sample(), new Sample());
		analysisSubmission = AnalysisSubmission.builder(UUID.randomUUID()).inputFiles(sequencingObjects).build();

		when(sampleService.getSamplesForAnalysisSubmission(analysisSubmission)).thenReturn(samples);
	}

	@Test
	public void testAllowed() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(true);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(true);

		assertTrue("Permission allowed",
				updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission));
	}

	@Test
	public void testDeniedReadAnalysisSubmission() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(false);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(true);

		assertFalse("Permission denied",
				updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission));
	}

	@Test
	public void testDeniedUpdateSamples() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(true);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(false);

		assertFalse("Permission denied",
				updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission));
	}

	@Test
	public void testDeniedBoth() {
		when(readAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission)).thenReturn(false);
		when(updateSamplePermission.isAllowed(authentication, samples)).thenReturn(false);

		assertFalse("Permission denied",
				updateSamplesFromAnalysisSubmissionPermission.isAllowed(authentication, analysisSubmission));
	}
}
