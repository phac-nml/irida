package ca.corefacility.bioinformatics.irida.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;

/**
 * Aspect used to create project events for methods annotated with event
 * annotations
 * 
 *
 * @see LaunchesProjectEvent
 */
@Aspect
public class ProjectEventAspect {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventAspect.class);
	private ProjectEventHandler eventHandler;

	public ProjectEventAspect(ProjectEventHandler eventListener) {
		this.eventHandler = eventListener;
	}

	@AfterReturning(value = "execution(public (!void) *(..)) &&  @annotation(eventAnnotation)", returning = "returnValue")
	public void handleProjectEvent(JoinPoint jp, LaunchesProjectEvent eventAnnotation, Object returnValue) {
		logger.trace("Intercepted method annotated with LaunchesProjectEvent " + jp.toString());
		eventHandler.delegate(new MethodEvent(eventAnnotation.value(), returnValue, jp.getArgs()));
	}

	@AfterReturning(value = "execution(public void *(..)) && @annotation(eventAnnotation)")
	public void handleProjectEventWithoutReturn(JoinPoint jp, LaunchesProjectEvent eventAnnotation) {
		logger.trace("Intercepted void method annotated with LaunchesProjectEvent " + jp.toString());
		eventHandler.delegate(new MethodEvent(eventAnnotation.value(), null, jp.getArgs()));
	}
}
