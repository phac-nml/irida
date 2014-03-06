package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MiseqServiceImplIT {
	@Autowired
	private MiseqRunService miseqRunService;
	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddSequenceFileToMiseqRunAsSequencer() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_SEQUENCER);
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddSequenceFileToMiseqRunAsAdmin() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_ADMIN);
	}

	@Test(expected = AccessDeniedException.class)
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddSequenceFileToMiseqRunAsUser() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_USER);
	}

	@Test(expected = AccessDeniedException.class)
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddSequenceFileToMiseqRunAsManager() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_MANAGER);
	}

	private void testAddSequenceFileToMiseqRunAsRole(Role r) {
		SequenceFile sf = asRole(r).sequenceFileService.read(1l);
		MiseqRun miseqRun = asRole(r).miseqRunService.read(1l);
		Join<MiseqRun, SequenceFile> j = asRole(r).miseqRunService.addSequenceFileToMiseqRun(miseqRun, sf);
		assertNotNull("Join was empty.", j);
		assertEquals("Join had wrong sequence file.", sf, j.getObject());
		assertEquals("Join had wrong miseq run.", miseqRun, j.getSubject());
	}

	@Test(expected = EntityExistsException.class)
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddSequenceFileToMiseqRunMultiple() {
		SequenceFile sf = asRole(Role.ROLE_SEQUENCER).sequenceFileService.read(1l);
		MiseqRun miseqRun = asRole(Role.ROLE_SEQUENCER).miseqRunService.read(1l);
		asRole(Role.ROLE_SEQUENCER).miseqRunService.addSequenceFileToMiseqRun(miseqRun, sf);
		asRole(Role.ROLE_SEQUENCER).miseqRunService.addSequenceFileToMiseqRun(miseqRun, sf);
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testGetMiseqRunForSequenceFile() {
		SequenceFile sf = asRole(Role.ROLE_ADMIN).sequenceFileService.read(2l);

		try {
			Join<MiseqRun, SequenceFile> j = asRole(Role.ROLE_ADMIN).miseqRunService.getMiseqRunForSequenceFile(sf);
			assertNotNull("Join was empty.", j);
			assertEquals("Join had wrong sequence file.", sf, j.getObject());
			assertEquals("Join had wrong miseq run.", Long.valueOf(2l), j.getSubject().getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed for unknown reason.");
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testCreateMiseqRunAsSequencer() {
		MiseqRun mr = new MiseqRun();
		mr.setProjectName("Project name.");
		mr = asRole(Role.ROLE_SEQUENCER).miseqRunService.create(mr);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testReadMiseqRunAsSequencer() {
		MiseqRun mr = asRole(Role.ROLE_SEQUENCER).miseqRunService.read(1L);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testAddFileToMiseqRunAsSequencer() {
		MiseqRun mr = asRole(Role.ROLE_SEQUENCER).miseqRunService.read(1L);
		SequenceFile sf = asRole(Role.ROLE_SEQUENCER).sequenceFileService.read(1l);
		Join<MiseqRun, SequenceFile> join = asRole(Role.ROLE_SEQUENCER).miseqRunService.addSequenceFileToMiseqRun(mr,
				sf);
		assertEquals("Wrong miseq run in join.", mr, join.getSubject());
		assertEquals("Wrong sequence file in join.", sf, join.getObject());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testCreateMiseqRunAsAdmin() {
		MiseqRun r = new MiseqRun();
		r.setProjectName("some project");
		asRole(Role.ROLE_ADMIN).miseqRunService.create(r);
	}
	
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/MiseqServiceImplIT.xml")
	public void testDeleteCascade(){
		assertTrue("Sequence file should exist before",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(1L));
		miseqRunService.delete(2L);
		assertFalse("Sequence file should be deleted on cascade",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(2L));
	}

	private MiseqServiceImplIT asRole(Role r) {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}
}
