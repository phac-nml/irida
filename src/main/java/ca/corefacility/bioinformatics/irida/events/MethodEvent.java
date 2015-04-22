package ca.corefacility.bioinformatics.irida.events;

import java.util.Arrays;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;

/**
 * Captures the arguments and return value of a method call. Also contains the
 * {@link ProjectEvent} class that should be created from this call.
 * 
 *
 */
public class MethodEvent {
	private Class<? extends ProjectEvent> eventClass;
	private Object returnValue;
	private Object[] args;

	public MethodEvent(Class<? extends ProjectEvent> eventClass, Object returnValue, Object[] args) {
		this.eventClass = eventClass;
		this.returnValue = returnValue;
		this.args = args;
	}

	/**
	 * Get the arguments from the method call
	 * 
	 * @return the args passed to the method that was called.
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Get the return value for the method call
	 * 
	 * @return the value returned by the method call
	 */
	public Object getReturnValue() {
		return returnValue;
	}

	/**
	 * Get the class of event to be created
	 * 
	 * @return the type of object that the method was called on.
	 */
	public Class<? extends ProjectEvent> getEventClass() {
		return eventClass;
	}

	@Override
	public String toString() {
		return "MethodEvent[ args= " + Arrays.toString(args) + ", returnValue= " + returnValue + ", eventClass= "
				+ eventClass + " ]";
	}
}
