package ca.corefacility.bioinformatics.irida.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.events.ProjectEventHandler;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;

/**
 * Annotation to be put on methods which should create a {@link ProjectEvent}.
 * Value must be some subclass of {@link ProjectEvent} to notify the event
 * handler which event should be created.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 * @see ProjectEventHandler
 * @see UserRoleSetProjectEvent
 * @see UserRemovedProjectEvent
 * @see SampleAddedProjectEvent
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LaunchesProjectEvent {
	/**
	 * The type of {@link ProjectEvent} that should be created on this annotated
	 * method
	 */
	Class<? extends ProjectEvent> value();
}
