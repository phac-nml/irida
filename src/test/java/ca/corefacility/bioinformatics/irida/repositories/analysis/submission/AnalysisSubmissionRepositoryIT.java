package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
	WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/remote/resttemplate/OAuthTokenRestTemplateIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionRepositoryIT {

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	
	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;
	
	@Autowired
	private ReferenceFileRepository referenceFileRepository;
	
	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	
	private Set<SequenceFile> sequenceFiles;
	private ReferenceFile referenceFile;
	
	private RemoteWorkflowPhylogenomics remoteWorkflow;
	
	@Before
	public void setup() throws IOException {
		Path sequenceFilePath = Files.createTempFile("test", ".fastq");
		Path referenceFilePath = Files.createTempFile("reference", ".fasta");
		
		SequenceFile sequenceFile = new SequenceFile(sequenceFilePath);
		sequenceFiles = Sets.newHashSet(sequenceFile);
		
		referenceFile = new ReferenceFile(referenceFilePath);
		
		String workflowId = "5f5";
		String workflowChecksum = "55";
		
		String inputSequenceFilesLabel = "input_sequences";
		String inputReferenceFileLabel = "input_reference";
		
		String outputPhylogeneticTreeName = "tree.txt";
		String outputSnpMatrixName = "matrix.tsv";
		String outputSnpTableName = "table.tsv";
		
		remoteWorkflow = 
				new RemoteWorkflowPhylogenomics(workflowId, workflowChecksum, inputSequenceFilesLabel,
						inputReferenceFileLabel, outputPhylogeneticTreeName, outputSnpMatrixName,
						outputSnpTableName);
	}
	
	@Test
	@WithMockUser(username = "tom", roles = "ADMIN")
	public void testSaveAnalysisSubmission() {
		AnalysisSubmissionPhylogenomics submission =
				new AnalysisSubmissionPhylogenomics(sequenceFiles, referenceFile,
						remoteWorkflow);
		submission.setRemoteAnalysisId("10");
		
		sequenceFileRepository.save(sequenceFiles);
		referenceFileRepository.save(referenceFile);
		remoteWorkflowRepository.save(remoteWorkflow);
		analysisSubmissionRepository.save(submission);
		
		AnalysisSubmissionPhylogenomics savedSubmission = 
				analysisSubmissionRepository.getByType("10", AnalysisSubmissionPhylogenomics.class);
		
		assertEquals(submission.getRemoteAnalysisId(), savedSubmission.getRemoteAnalysisId());
		assertEquals(submission.getRemoteWorkflow(), savedSubmission.getRemoteWorkflow());
		assertEquals(submission.getInputFiles(), savedSubmission.getInputFiles());
		assertEquals(submission.getReferenceFile(), savedSubmission.getReferenceFile());
	}
}
