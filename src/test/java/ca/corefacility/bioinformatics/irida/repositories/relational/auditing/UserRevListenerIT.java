package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.envers.AuditReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.security.annotations.WithMockOAuth2Client;

@ServiceIntegrationTest
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

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId())
				.orElse(null);
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber().orElse(null));
		assertEquals(Long.valueOf(1), findRevision.getClientId(), "client id should be set in revision");
	}

	@Test
	@WithMockUser(username = "fbristow", password = "Password1!")
	public void testModifyWithUsernamePassword() {
		Project read = projectRepository.findById(1L).orElse(null);
		read.setName("A new name");
		projectRepository.save(read);

		Revision<Integer, Project> findLastChangeRevision = projectRepository.findLastChangeRevision(read.getId())
				.orElse(null);
		UserRevEntity findRevision = auditReader.findRevision(UserRevEntity.class,
				findLastChangeRevision.getRevisionNumber().orElse(null));
		assertNull(findRevision.getClientId());
	}
}
