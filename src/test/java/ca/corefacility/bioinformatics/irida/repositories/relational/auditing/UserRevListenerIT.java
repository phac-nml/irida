package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hibernate.envers.AuditReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class, })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/relational/auditing/UserRevListenerIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserRevListenerIT {
	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	AuditReader auditReader;

	@Autowired
	PasswordEncoder passwordEncoder;

	private String clientId = "testClient";

	OAuth2Authentication oAuth;

	UsernamePasswordAuthenticationToken auth;

	@Before
	public void setUp() {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(Role.ROLE_USER);
		auth = new UsernamePasswordAuthenticationToken(u, "Password1", ImmutableList.of(Role.ROLE_USER));
		auth.setDetails(u);

		AuthorizationRequest authRequest = mock(AuthorizationRequest.class);

		oAuth = new OAuth2Authentication(authRequest, auth);
		when(authRequest.getClientId()).thenReturn(clientId);
	}

	@Test
	public void testModifyWithOAuth2() {
		SecurityContextHolder.getContext().setAuthentication(oAuth);

		Project read = projectRepository.findOne(1l);
		read.setName("A new name");
		projectRepository.save(read);

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId());
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber());
		assertEquals("client id should be set in revision", clientId, findRevision.getClientId());
	}

	@Test
	public void testModifyWithUsernamePassword() {
		SecurityContextHolder.getContext().setAuthentication(auth);

		Project read = projectRepository.findOne(1l);
		read.setName("A new name");
		projectRepository.save(read);

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId());
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber());
		assertNull(findRevision.getClientId());
	}
}
