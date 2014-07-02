package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.SequencingRunEntity;
import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * Test for SequencingRunServiceImplIT.
 * NOTE: This class uses a separate table reset file at 
 * /ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceTableReset.xml
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class, WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceImplIT.xml")
@DatabaseTearDown({"/ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceTableReset.xml"})
public class SequencingRunServiceImplIT {
	@Autowired
	private SequencingRunService miseqRunService;
	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void testAddSequenceFileToMiseqRunAsSequencer() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_SEQUENCER);
	}

	@Test
	public void testAddSequenceFileToMiseqRunAsAdmin() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_ADMIN);
	}

	@Test(expected = AccessDeniedException.class)
	public void testAddSequenceFileToMiseqRunAsUser() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_USER);
	}

	@Test(expected = AccessDeniedException.class)
	public void testAddSequenceFileToMiseqRunAsManager() {
		testAddSequenceFileToMiseqRunAsRole(Role.ROLE_MANAGER);
	}

	private void testAddSequenceFileToMiseqRunAsRole(Role r) {
		SequenceFile sf = asRole(r).sequenceFileService.read(1l);
		SequencingRun miseqRun = asRole(r).miseqRunService.read(1l);
		asRole(r).miseqRunService.addSequenceFileToSequencingRun(miseqRun, sf);
		SequencingRun saved = asRole(r).miseqRunService.read(1l);
		Set<SequenceFile> sequenceFilesForMiseqRun = sequenceFileService.getSequenceFilesForSequencingRun(saved);
		assertTrue("Saved miseq run should have seqence file", sequenceFilesForMiseqRun.contains(sf));
	}

	@Test
	public void testGetMiseqRunForSequenceFile() {
		SequenceFile sf = asRole(Role.ROLE_ADMIN).sequenceFileService.read(2l);

		try {
			SequencingRun j = asRole(Role.ROLE_ADMIN).miseqRunService.getSequencingRunForSequenceFile(sf);
			assertEquals("Join had wrong miseq run.", Long.valueOf(2l), j.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed for unknown reason.");
		}
	}

	@Test
	public void testCreateMiseqRunAsSequencer() {
		MiseqRun mr = new MiseqRun();
		mr.setWorkflow("Workflow name.");
		SequencingRun returned = asRole(Role.ROLE_SEQUENCER).miseqRunService.create(mr);
		assertNotNull("Created run was not assigned an ID.", returned.getId());
	}

	@Test
	public void testReadMiseqRunAsSequencer() {
		SequencingRun mr = asRole(Role.ROLE_SEQUENCER).miseqRunService.read(1L);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}

	@Test
	public void testCreateMiseqRunAsAdmin() {
		MiseqRun r = new MiseqRun();
		r.setWorkflow("some workflow");
		asRole(Role.ROLE_ADMIN).miseqRunService.create(r);
	}
	
	@Test
	public void testDeleteCascadeToSequenceFile(){
		assertTrue("Sequence file should exist before",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(2L));
		miseqRunService.delete(2L);
		assertFalse("Sequence file should be deleted on cascade",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(2L));
	}
	
	@Test
	public void testDeleteCascadeToSample(){
		assertTrue("Sequence file should exist before",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(1L));
		miseqRunService.delete(3L);
		assertFalse("Sequence file should be deleted on cascade",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(1L));
		assertFalse("Sequence file should be deleted on cascade",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(3L));
		assertFalse("Sequence file should be deleted on cascade",asRole(Role.ROLE_ADMIN).sequenceFileService.exists(4L));
		assertFalse("Sample should be deleted on cascade", sampleService.exists(2L));
		assertTrue("This sample should not be removed", sampleService.exists(1L));
	}
	
	@Test
	@WithMockUser(username="fbristow", password="password1", roles="ADMIN")
	public void testListAllSequencingRuns(){
		Iterable<SequencingRun> findAll = miseqRunService.findAll();
		assertNotNull(findAll);
		boolean foundMiseq = false;
		boolean foundTestEntity = false;
		for(SequencingRun run : findAll){
			assertNotNull(run);
			if(run instanceof MiseqRun){
				foundMiseq = true;
			}
			else if(run instanceof SequencingRunEntity){
				foundTestEntity = true;
			}
		}
		
		assertTrue(foundMiseq);
		assertTrue(foundTestEntity);
	}
	
	/**
	 * This test simulates a bug that happens from the REST API when uploading sequence files to samples, 
	 * where a new sequence file is created, then detached from a transaction.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAddDetachedRunToSequenceFile() throws IOException{
		final String SEQUENCE = "ACGTACGTN";
		final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
				+ SEQUENCE + "\n+\n?????????").getBytes();
		Path p = Files.createTempFile(null,  null);
		Files.write(p, FASTQ_FILE_CONTENTS);
		
		SequenceFile sf = new SequenceFile();
		sf.setFile(p);
		Sample sample = asRole(Role.ROLE_ADMIN).sampleService.read(1L);
		SequencingRun run = asRole(Role.ROLE_ADMIN).miseqRunService.read(2L);
		
		asRole(Role.ROLE_ADMIN).sequenceFileService.createSequenceFileInSample(sf, sample);
		
		miseqRunService.addSequenceFileToSequencingRun(run, sf);
		
	}

	private SequencingRunServiceImplIT asRole(Role r) {
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
