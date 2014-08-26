package ca.corefacility.bioinformatics.irida.analysis.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.manager.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

public class AnalysisWithGalaxyMain {

	private static AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;
	
	private static RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;
	
	private static SequenceFileRepository sequenceFileRepository;
	
	private static ReferenceFileRepository referenceFileRepository;
	
	private static UserRepository userRepository;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, ExecutionManagerException {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.getEnvironment().setActiveProfiles("dev");
			context.register(IridaApiServicesConfig.class);
			context.register(AnalysisExecutionServiceConfig.class);
			context.register(ExecutionManagerConfig.class);
			context.register(RemoteWorkflowServiceConfig.class);
			context.refresh();
			
			analysisExecutionServicePhylogenomics
				= context.getBean(AnalysisExecutionServicePhylogenomics.class);
			
			remoteWorkflowServicePhylogenomics
				= context.getBean(RemoteWorkflowServicePhylogenomics.class);
			
			sequenceFileRepository
				= context.getBean(SequenceFileRepository.class);
			
			referenceFileRepository
				= context.getBean(ReferenceFileRepository.class);
			
			userRepository
				= context.getBean(UserRepository.class);
			
			User adminUser = userRepository.loadUserByUsername("admin");
			Authentication authentication = new UsernamePasswordAuthenticationToken(adminUser, "password1");
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			List<Path> sequenceFiles = new ArrayList<>();
			Files.walk(Paths.get("/home/aaron/tmp/cholera-files-subsample/fastq")).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			        sequenceFiles.add(filePath);
			    }
			});
			List<Path> sequenceFilePaths = createTemporaryFilesFrom(sequenceFiles);
			
			Path rf = Paths.get("/home/aaron/tmp/cholera-files-subsample/reference/2010EL-1786-c1_2000_2400kb.fasta");			
			Path referenceFile = createTempFileFor(rf);
			
			RemoteWorkflowPhylogenomics remoteWorkflow = remoteWorkflowServicePhylogenomics.getCurrentWorkflow();

			AnalysisSubmissionPhylogenomics submission = setupSubmissionInDatabase(sequenceFilePaths,
					referenceFile, remoteWorkflow);
			
			analysisExecutionServicePhylogenomics.executeAnalysis(submission);
		}
	}
	
	private static Path createTempFileFor(Path file) throws IOException {
		String filename = file.getFileName().toString();
		Path temp = Files.createTempFile(filename, ".fastq");
		temp.toFile().delete();
		Files.createLink(temp, file);
		return temp;
	}
	
	private static List<Path> createTemporaryFilesFrom(List<Path> files) throws IOException {
		List<Path> tempFiles = new ArrayList<>();
		for (Path file : files) {
			tempFiles.add(createTempFileFor(file));
		}
		
		return tempFiles;
	}
	
	private static ReferenceFile saveReferenceFile(Path referenceFilePath) {
		return referenceFileRepository.save(new ReferenceFile(referenceFilePath));
	}
	
	private static Set<SequenceFile> saveSequenceFiles(List<Path> sequenceFilesList) {
		Set<SequenceFile> sequenceFiles = new HashSet<>();
		
		for (Path file: sequenceFilesList) {
			SequenceFile sequenceFile = sequenceFileRepository.save(new SequenceFile(file));
			sequenceFiles.add(sequenceFile);
		}
		
		return sequenceFiles;
	}
	
	private static AnalysisSubmissionPhylogenomics setupSubmissionInDatabase(List<Path> sequenceFilesList,
			Path referenceFilePath, RemoteWorkflowPhylogenomics remoteWorkflow) {
		
		Set<SequenceFile> sequenceFiles = saveSequenceFiles(sequenceFilesList);
		ReferenceFile referenceFile = saveReferenceFile(referenceFilePath);
		
		return new AnalysisSubmissionPhylogenomics(sequenceFiles,
				referenceFile, remoteWorkflow);
	}
}
