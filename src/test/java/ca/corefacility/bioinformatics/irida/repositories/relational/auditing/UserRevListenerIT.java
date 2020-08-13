package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.envers.AuditReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.security.annotations.WithMockOAuth2Client;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/relational/auditing/UserRevListenerIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserRevListenerIT {
	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	AuditReader auditReader;

	@Test
	@WithMockOAuth2Client(clientId = "testClient", username = "fbristow", password = "Password1!")
	public void testModifyWithOAuth2() {

		Project read = projectRepository.findById(1L).orElse(null);
		read.setName("A new name");
		projectRepository.save(read);

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId()).orElse(null);
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber().orElse(null));
		assertEquals("client id should be set in revision", Long.valueOf(1), findRevision.getClientId());
	}

	@Test
	@WithMockUser(username = "fbristow", password = "Password1!")
	public void testModifyWithUsernamePassword() {
		Project read = projectRepository.findById(1L).orElse(null);
		read.setName("A new name");
		projectRepository.save(read);

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId()).orElse(null);
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber().orElse(null));
		assertNull(findRevision.getClientId());
	}
}
