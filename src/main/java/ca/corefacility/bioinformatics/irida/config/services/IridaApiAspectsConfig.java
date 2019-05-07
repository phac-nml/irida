package ca.corefacility.bioinformatics.irida.config.services;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import ca.corefacility.bioinformatics.irida.events.ProjectEventAspect;
import ca.corefacility.bioinformatics.irida.events.ProjectEventHandler;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.annotations.RunAsUserAspect;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect;
import ca.corefacility.bioinformatics.irida.validators.ValidMethodParametersAspect;
import ca.corefacility.bioinformatics.irida.service.EmailController;

/**
 * Configures the aspects in IRIDA
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class IridaApiAspectsConfig {

	@Bean
	public ValidMethodParametersAspect validMethodsParametersAspect(Validator validator) {
		return new ValidMethodParametersAspect(validator);
	}

	@Bean
	public ProjectEventAspect projectEventAspect(final ProjectEventRepository eventRepository,
			final ProjectSampleJoinRepository psjRepository, final ProjectRepository projectRepository,
			final SampleRepository sampleRepository) {
		return new ProjectEventAspect(new ProjectEventHandler(eventRepository, psjRepository, projectRepository, sampleRepository));
	}

	@Bean
	public AnalysisExecutionServiceAspect analysisExecutionServiceAspect(
			AnalysisSubmissionRepository analysisSubmissionRepository, EmailController emailController) {
		return new AnalysisExecutionServiceAspect(analysisSubmissionRepository, emailController);
	}

	/**
	 * Aspect for setting the user in the security context to be the user in the
	 * {@link AnalysisSubmission}
	 * 
	 * @return new {@link RunAsUserAspect} bean
	 */
	@Bean
	public RunAsUserAspect runAsSubmissionUserAspect() {
		return new RunAsUserAspect();
	}
}
