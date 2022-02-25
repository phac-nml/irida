package ca.corefacility.bioinformatics.irida.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import ca.corefacility.bioinformatics.irida.config.repository.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;

/**
 * Aspect used to create project events for methods annotated with event annotations
 *
 * @see LaunchesProjectEvent
 */
@Aspect
public class ProjectEventAspect implements Ordered {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventAspect.class);
	private ProjectEventHandler eventHandler;

	public ProjectEventAspect(ProjectEventHandler eventListener) {
		this.eventHandler = eventListener;
	}

	/**
	 * Get the return value of a method to send to the {@link ProjectEventHandler}
	 *
	 * @param jp              the JoinPoint object describing the method signature
	 * @param eventAnnotation the LaunchesProjectEvent annotation arguments
	 * @param returnValue     the return value of the annotated method
	 */
	@AfterReturning(value = "execution(public (!void) *(..)) &&  @annotation(eventAnnotation)",
			returning = "returnValue")
	public void handleProjectEvent(JoinPoint jp, LaunchesProjectEvent eventAnnotation, Object returnValue) {
		logger.trace("Intercepted method annotated with LaunchesProjectEvent " + jp.toString());
		eventHandler.delegate(new MethodEvent(eventAnnotation.value(), returnValue, jp.getArgs()));
	}

	/**
	 * Get the arguments of a method to send to the {@link ProjectEventHandler}
	 *
	 * @param jp              The join point object describing the method signature
	 * @param eventAnnotation The LaunchesProjectEvent annotation arguments
	 */
	@AfterReturning(value = "execution(public void *(..)) && @annotation(eventAnnotation)")
	public void handleProjectEventWithoutReturn(JoinPoint jp, LaunchesProjectEvent eventAnnotation) {
		logger.trace("Intercepted void method annotated with LaunchesProjectEvent " + jp.toString());
		eventHandler.delegate(new MethodEvent(eventAnnotation.value(), null, jp.getArgs()));
	}

	/**
	 * This event aspect can happen within a transaction as we are only updating the modifiedDate within a project,
	 * which should be near instaneous.
	 *
	 * @return the order of this aspect
	 */
	@Override
	public int getOrder() {
		return IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER;
	}
}
