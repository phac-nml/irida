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
	 * This event **must** happen outside of a transaction so that multiple events happening at the same time do not
	 * result in a deadlock exception. Example: when multiple sequencers are uploading sample data simultaneously, they
	 * are all creating samples on the server. When the samples are being created the project event for updating the
	 * project modified time is fired many times simultaneously, so multiple clients are trying to update the project at
	 * the same time. If that update is within a transaction, at least one of those simultaneous updates fails, so the
	 * client gets a report that creating the sample failed. Since we don't care which thread wins the update (they
	 * should be setting the same updated time down to at least the second), just make sure that this aspect is being
	 * applied *outside* of the transaction.
	 *
	 * @return the order of this aspect
	 */
	@Override
	public int getOrder() {
		return IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER - 1;
	}
}
