package ca.corefacility.bioinformatics.irida.events.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LaunchesProjectEvent {
	Class<? extends ProjectEvent> value();
}
