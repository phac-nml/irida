package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

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

/**
 * Aspect to update the Authentication object in the SecurityContext to be the
 * submitter of the {@link AnalysisSubmission}
 * 
 * @see RunAsSubmissionUser
 */
@Aspect
public class RunAsSubmissionUserAspect {
	private static final Logger logger = LoggerFactory.getLogger(RunAsSubmissionUserAspect.class);

	/**
	 * Advice around a method that has an {@link AnalysisSubmission} as an
	 * argument. This method will set the {@link Authentication} in the
	 * {@link SecurityContext} to be the submitting user, run the method, then
	 * reset the {@link Authentication} afterwards.
	 * 
	 * @param jp
	 *            {@link ProceedingJoinPoint} for the called method
	 * @param eventAnnotation
	 *            The annotation on the method called
	 * @return Return value of the method called
	 * @throws Throwable
	 *             if the method throws an exception
	 */
	@Around(value = "execution(* *(..)) && @annotation(eventAnnotation) && args(submission)")
	public Object setSecurityContextFromAnalysisSubmission(ProceedingJoinPoint jp, AnalysisSubmission submission,
			RunAsSubmissionUser eventAnnotation) throws Throwable {
		logger.trace("Updating user authentication for submission" + submission.getId());
		SecurityContext context = SecurityContextHolder.getContext();

		Authentication originalAuthentication = context.getAuthentication();

		logger.trace("Original user: " + originalAuthentication.getName());

		User submitter = submission.getSubmitter();

		logger.trace("Setting user " + submitter.getUsername());

		PreAuthenticatedAuthenticationToken submitterAuthenticationToken = new PreAuthenticatedAuthenticationToken(
				submitter, null, Lists.newArrayList(submitter.getSystemRole()));

		context.setAuthentication(submitterAuthenticationToken);
		SecurityContextHolder.setContext(context);

		Object returnValue = null;
		try {
			returnValue = jp.proceed();
		} finally {
			logger.trace("Resetting authentication to " + originalAuthentication.getName());

			context.setAuthentication(originalAuthentication);
			SecurityContextHolder.setContext(context);
		}

		return returnValue;
	}

}
