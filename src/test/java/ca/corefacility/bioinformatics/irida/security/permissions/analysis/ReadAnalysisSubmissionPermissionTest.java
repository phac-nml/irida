package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.files.ReadSequencingObjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ReadProjectPermission;

import com.google.common.collect.ImmutableList;
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
	private SequencingObjectRepository sequencingObjectRepository;

	@Mock
	ProjectAnalysisSubmissionJoinRepository pasRepository;

	@Mock
	ReadProjectPermission readProjectPermission;

	@Mock
	private ReferenceFile referenceFile;

	private UUID workflowId = UUID.randomUUID();

	private Set<SequencingObject> inputSingleFiles;

	@Mock
	private ReadSequencingObjectPermission seqObjectPermission;

	/**
	 * Setup for tests
	 */
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		readAnalysisSubmissionPermission = new ReadAnalysisSubmissionPermission(analysisSubmissionRepository,
				userRepository, sequencingObjectRepository, seqObjectPermission, pasRepository, readProjectPermission);

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
				.inputFiles(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(u);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));

		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission was not granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findById(1L);
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
				.inputFiles(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(u);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));

		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, analysisSubmission),
				"permission was not granted.");

		verify(userRepository).loadUserByUsername(username);
		verifyNoInteractions(analysisSubmissionRepository);
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
				.inputFiles(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));

		assertFalse(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission was not granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findById(1L);
	}

	/**
	 * Tests granting permission for an admin to read an analysis submission by
	 * the submission id.
	 */
	@Test
	public void testPermitAdmin() {
		Collection<GrantedAuthority> roles = Lists.newArrayList(Role.ROLE_ADMIN);

		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1", roles);

		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(inputSingleFiles).referenceFile(referenceFile).build()));
		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission was not granted to admin.");

		// we should fast pass through to permission granted for administrators.
		verifyNoInteractions(userRepository);
	}

	@Test
	public void testPermitAutoAssembly() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		Project p = new Project();

		SequenceFilePair pair = new SequenceFilePair();

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(ImmutableSet.of(pair)).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());
		pair.setAutomatedAssembly(analysisSubmission);

		/*
		 * testing that analysis is shared with a project that user isn't a part
		 * of
		 */
		when(pasRepository.getProjectsForSubmission(analysisSubmission))
				.thenReturn(ImmutableList.of(new ProjectAnalysisSubmissionJoin(p, analysisSubmission)));
		when(readProjectPermission.customPermissionAllowed(auth, p)).thenReturn(false);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));
		when(seqObjectPermission.customPermissionAllowed(auth, pair)).thenReturn(true);
		when(sequencingObjectRepository.findSequencingObjectsForAnalysisSubmission(analysisSubmission))
				.thenReturn(ImmutableSet.of(pair));

		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission should be granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findById(1L);
		verify(seqObjectPermission).customPermissionAllowed(auth, pair);
	}
	
	@Test
	public void testPermitSISTR() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		Project p = new Project();

		SequenceFilePair pair = new SequenceFilePair();

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(ImmutableSet.of(pair)).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());
		pair.setSistrTyping(analysisSubmission);

		/*
		 * testing that analysis is shared with a project that user isn't a part
		 * of
		 */
		when(pasRepository.getProjectsForSubmission(analysisSubmission))
				.thenReturn(ImmutableList.of(new ProjectAnalysisSubmissionJoin(p, analysisSubmission)));
		when(readProjectPermission.customPermissionAllowed(auth, p)).thenReturn(false);

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));
		when(seqObjectPermission.customPermissionAllowed(auth, pair)).thenReturn(true);
		when(sequencingObjectRepository.findSequencingObjectsForAnalysisSubmission(analysisSubmission))
				.thenReturn(ImmutableSet.of(pair));

		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission should be granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findById(1L);
		verify(seqObjectPermission).customPermissionAllowed(auth, pair);
	}

	@Test
	public void testPermitProjectShare() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		Project p = new Project();

		AnalysisSubmission analysisSubmission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(inputSingleFiles).referenceFile(referenceFile).build();
		analysisSubmission.setSubmitter(new User());

		when(pasRepository.getProjectsForSubmission(analysisSubmission))
				.thenReturn(ImmutableList.of(new ProjectAnalysisSubmissionJoin(p, analysisSubmission)));

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(analysisSubmissionRepository.findById(1L)).thenReturn(Optional.of(analysisSubmission));

		when(readProjectPermission.customPermissionAllowed(auth, p)).thenReturn(true);

		assertTrue(readAnalysisSubmissionPermission.isAllowed(auth, 1L),
				"permission should be granted.");

		verify(userRepository).loadUserByUsername(username);
		verify(analysisSubmissionRepository).findById(1L);
	}

}
