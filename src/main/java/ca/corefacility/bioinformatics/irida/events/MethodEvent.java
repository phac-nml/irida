package ca.corefacility.bioinformatics.irida.events;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;

/**
 * Captures the arguments and return value of a method call. Also contains the
 * {@link ProjectEvent} class that should be created from this call.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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
	 * @return
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Get the return value for the method call
	 * 
	 * @return
	 */
	public Object getReturnValue() {
		return returnValue;
	}

	/**
	 * Get the class of event to be created
	 * 
	 * @return
	 */
	public Class<? extends ProjectEvent> getEventClass() {
		return eventClass;
	}

	@Override
	public String toString() {
		return "MethodEvent[ args= " + args + ", returnValue= " + returnValue + ", eventClass= " + eventClass + " ]";
	}
}
