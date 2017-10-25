package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.config.repository.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Aspect to update the Authentication object in the SecurityContext to be the
 * user specified in the annotation using SpEL.
 * 
 * @see RunAsUser
 */
@Aspect
@Order(value=RunAsUserAspect.RUN_AS_USER_ORDER)
public class RunAsUserAspect {
	
	public static final int RUN_AS_USER_ORDER = IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER - 1;
	
	private static final Logger logger = LoggerFactory.getLogger(RunAsUserAspect.class);

	/**
	 * Advice around a method annotated with {@link RunAsUser}. This method will
	 * set the {@link User} specified in the {@link RunAsUser#value()} using
	 * SpEL in the security context before the method is run, then reset the
	 * original user after the method completes.
	 * 
	 * @param jp
	 *            {@link ProceedingJoinPoint} for the called method
	 * @param userAnnotation
	 *            {@link RunAsUser} annotation specifying the user
	 * @return Return value of the method called
	 * @throws Throwable
	 *             if the method throws an exception
	 */
	@Around(value = "execution(* *(..)) && @annotation(userAnnotation)")
	public Object setSecurityContextFromAnalysisSubmission(ProceedingJoinPoint jp, RunAsUser userAnnotation)
			throws Throwable {

		// Get the method arguments and apply them to an evaluation context
		MethodSignature signature = (MethodSignature) jp.getSignature();
		String[] parameterNames = signature.getParameterNames();
		Object[] args = jp.getArgs();

		StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

		for (int i = 0; i < args.length; i++) {
			String name = parameterNames[i];
			Object val = args[i];
			evaluationContext.setVariable(name, val);
		}

		// get the expression from the annotation and apply it to the evaluation
		// context
		String expression = userAnnotation.value();
		ExpressionParser parser = new SpelExpressionParser();

		Expression parseExpression = parser.parseExpression(expression);

		Object expressionValue = parseExpression.getValue(evaluationContext);

		if (!(expressionValue instanceof User)) {
			throw new IllegalArgumentException("RunAsUser value must refer to a User");
		}

		User submitter = (User) expressionValue;

		// get the original security context
		logger.trace("Updating user authentication");
		SecurityContext originalConext = SecurityContextHolder.getContext();

		logger.trace("Original user: " + originalConext.getAuthentication().getName());
		logger.trace("Setting user " + submitter.getUsername());

		Object returnValue = null;
		try {
			// set the new user authentication
			PreAuthenticatedAuthenticationToken submitterAuthenticationToken = new PreAuthenticatedAuthenticationToken(
					submitter, null, Lists.newArrayList(submitter.getSystemRole()));
			SecurityContext newContext = SecurityContextHolder.createEmptyContext();
			newContext.setAuthentication(submitterAuthenticationToken);
			SecurityContextHolder.setContext(newContext);

			// run the method
			returnValue = jp.proceed();
		} finally {
			// return the old authentication
			logger.trace("Resetting authentication to " + originalConext.getAuthentication().getName());

			SecurityContextHolder.setContext(originalConext);
		}

		return returnValue;
	}

}
