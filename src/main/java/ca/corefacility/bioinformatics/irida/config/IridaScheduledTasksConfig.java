package ca.corefacility.bioinformatics.irida.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Config for only activating scheduled tasks in certain profiles.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Profile({ "dev", "prod", "it" })
@Configuration
@EnableScheduling
public class IridaScheduledTasksConfig {

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Autowired
	private UserService userService;

	@Bean
	public AnalysisExecutionScheduledTask analysisExecutionScheduledTask() {
		Authentication anonymousToken = new AnonymousAuthenticationToken(
				"nobody", "nobody", ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);

		User admin = userService.getUserByUsername("admin");

		Authentication adminToken = new PreAuthenticatedAuthenticationToken(
				admin, null, Lists.newArrayList(Role.ROLE_ADMIN));

		return new AnalysisExecutionScheduledTaskImpl(
				analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServicePhylogenomics, adminToken);
	}
}
