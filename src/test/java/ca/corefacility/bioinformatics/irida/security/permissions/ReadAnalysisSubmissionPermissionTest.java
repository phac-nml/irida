package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Tests for {@link ReadAnalysisSubmissionPermission}.
 * 
 *
 */
public class ReadAnalysisSubmissionPermissionTest {

	private ReadAnalysisSubmissionPermission readAnalysisSubmissionPermission;

	@Mock
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ReferenceFile referenceFile;

	private UUID workflowId = UUID.randomUUID();

	private Set<SingleEndSequenceFile> inputSingleFiles;

	@Mock
	private ReadSequencingObjectPermission seqObjectPermission;

	/**
	 * Setup for tests
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		readAnalysisSubmissionPermission = new ReadAnalysisSubmissionPermission(analysisSubmissionRepository,
				userRepository, seqObjectPermission);

		inputSingleFiles = Sets.newHashSet(new SingleEndSequenceFile(new SequenceFile()));
	}

	/**
	 * Tests granting permission for a user to read an analysis submission by
	 * the submission id.
	 */
	@Test
	public void testGrantPermission() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFilesSingleEnd(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(u);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findOne(1L)).thenReturn(analysisSubmission);

		assertTrue("permission was not granted.", readAnalysisSubmissionPermission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findOne(1L);
	}

	/**
	 * Tests granting permission for a user to read an analysis submission by
	 * the submission object.
	 */
	@Test
	public void testGrantPermissionWithDomainObject() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFilesSingleEnd(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(u);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findOne(1L)).thenReturn(analysisSubmission);

		assertTrue("permission was not granted.", readAnalysisSubmissionPermission.isAllowed(auth, analysisSubmission));

		verify(userRepository).loadUserByUsername(username);
		verifyZeroInteractions(analysisSubmissionRepository);
	}

	/**
	 * Tests rejecting permission for a user to read an analysis submission by
	 * the submission id.
	 */
	@Test
	public void testRejectPermission() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFilesSingleEnd(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findOne(1L)).thenReturn(analysisSubmission);

		assertFalse("permission was not granted.", readAnalysisSubmissionPermission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findOne(1L);
	}

	/**
	 * Tests granting permission for an admin to read an analysis submission by
	 * the submission id.
	 */
	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = Lists.newArrayList(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1", roles);

		when(analysisSubmissionRepository.findOne(1L)).thenReturn(AnalysisSubmission.builder(workflowId).name("test")
				.inputFilesSingleEnd(inputSingleFiles).referenceFile(referenceFile).build());
		assertTrue("permission was not granted to admin.", readAnalysisSubmissionPermission.isAllowed(auth, 1L));

		// we should fast pass through to permission granted for administrators.
		verifyZeroInteractions(userRepository);
	}

	@Test
	public void testPermitAutoAssembly() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		SequenceFilePair pair = new SequenceFilePair();

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFilesPaired(ImmutableSet.of(pair)).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());
		pair.setAutomatedAssembly(analysisSubmission);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findOne(1L)).thenReturn(analysisSubmission);
		when(seqObjectPermission.customPermissionAllowed(auth, pair)).thenReturn(true);

		assertTrue("permission should be granted.", readAnalysisSubmissionPermission.isAllowed(auth, 1L));

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findOne(1L);
		verify(seqObjectPermission).customPermissionAllowed(auth, pair);
	}

}
