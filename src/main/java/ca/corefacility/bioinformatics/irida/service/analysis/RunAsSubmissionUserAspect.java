package ca.corefacility.bioinformatics.irida.service.analysis;

import java.util.concurrent.Future;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

@Aspect
public class RunAsSubmissionUserAspect {
	private static final Logger logger = LoggerFactory.getLogger(RunAsSubmissionUserAspect.class);

	@Around(value = "execution(* *(..)) && @annotation(eventAnnotation)")
	public Object setSecurityContextFromAnalysisSubmission(ProceedingJoinPoint jp, RunAsSubmissionUser eventAnnotation)
			throws Throwable {
		SecurityContext context = SecurityContextHolder.getContext();

		Authentication originalAuthentication = context.getAuthentication();

		logger.debug("Original user: " + originalAuthentication.getName());

		Object[] args = jp.getArgs();

		AnalysisSubmission submission = null;
		for (Object arg : args) {
			if (arg instanceof AnalysisSubmission) {
				submission = (AnalysisSubmission) arg;
			}
		}

		logger.debug("Working with submission " + submission);

		User submitter = submission.getSubmitter();

		logger.debug("Setting user " + submitter.getUsername());

		PreAuthenticatedAuthenticationToken submitterAuthenticationToken = new PreAuthenticatedAuthenticationToken(
				submitter, null, Lists.newArrayList(submitter.getSystemRole()));

		context.setAuthentication(submitterAuthenticationToken);
		SecurityContextHolder.setContext(context);

		Object returnValue = jp.proceed();

		logger.debug("Resetting authentication");

		context.setAuthentication(originalAuthentication);
		SecurityContextHolder.setContext(context);

		return returnValue;
	}

}
