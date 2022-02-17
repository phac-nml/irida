package ca.corefacility.bioinformatics.irida.security.permissions.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;

import com.google.common.collect.ImmutableList;

public class UpdateSamplePermissionTest {

	private ProjectSampleJoinRepository projectSampleJoinRepository;
	private ProjectOwnerPermission projectOwnerPermission;
	private SampleRepository sampleRepository;
	private UpdateSamplePermission updateSamplePermission;
	private Authentication auth;

	@BeforeEach
	public void setUp() {
		projectSampleJoinRepository = mock(ProjectSampleJoinRepository.class);
		projectOwnerPermission = mock(ProjectOwnerPermission.class);
		sampleRepository = mock(SampleRepository.class);
		updateSamplePermission = new UpdateSamplePermission(sampleRepository, projectOwnerPermission,
				projectSampleJoinRepository);
		Collection<GrantedAuthority> roles = new ArrayList<>();
		roles.add(Role.ROLE_USER);

		auth = new UsernamePasswordAuthenticationToken("fbristow", "password1", roles);
	}

	@Test
	public void testGrantPermission() {
		final Project p1 = new Project();
		final Project p2 = new Project();
		final Sample s = new Sample();

		when(projectSampleJoinRepository.getProjectForSample(s))
				.thenReturn(ImmutableList.of(new ProjectSampleJoin(p1, s, true), new ProjectSampleJoin(p2, s, true)));
		when(projectOwnerPermission.isAllowed(auth, p1)).thenReturn(true);
		when(projectOwnerPermission.isAllowed(auth, p2)).thenReturn(true);

		assertTrue(updateSamplePermission.isAllowed(auth, s), "Permission to update sample should be given.");
	}

	@Test
	public void testGrantPermissionWithOneProject() {
		final Project p1 = new Project();
		final Project p2 = new Project();
		final Sample s = new Sample();

		when(projectSampleJoinRepository.getProjectForSample(s))
				.thenReturn(ImmutableList.of(new ProjectSampleJoin(p1, s, true), new ProjectSampleJoin(p2, s, true)));
		when(projectOwnerPermission.isAllowed(auth, p1)).thenReturn(false);
		when(projectOwnerPermission.isAllowed(auth, p2)).thenReturn(true);

		assertTrue(updateSamplePermission.isAllowed(auth, s), "Permission to update sample should be given.");
	}

	@Test
	public void testRejectPermissionWithNoProjects() {
		final Project p1 = new Project();
		final Project p2 = new Project();
		final Sample s = new Sample();

		when(projectSampleJoinRepository.getProjectForSample(s))
				.thenReturn(ImmutableList.of(new ProjectSampleJoin(p1, s, true), new ProjectSampleJoin(p2, s, true)));
		when(projectOwnerPermission.isAllowed(auth, p1)).thenReturn(false);
		when(projectOwnerPermission.isAllowed(auth, p2)).thenReturn(false);

		assertFalse(updateSamplePermission.isAllowed(auth, s), "Permission to update sample should not be given.");
	}

	@Test
	public void testRejectPermissionNotOwner() {
		final Project p1 = new Project();
		final Sample s = new Sample();

		when(projectSampleJoinRepository.getProjectForSample(s))
				.thenReturn(ImmutableList.of(new ProjectSampleJoin(p1, s, false)));
		when(projectOwnerPermission.isAllowed(auth, p1)).thenReturn(true);

		assertFalse(updateSamplePermission.isAllowed(auth, s), "Permission to update sample should not be given.");
	}
}
