package ca.corefacility.bioinformatics.irida.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;

/**
 * Handles the creation of {@link ProjectEvent}s from methods annotated with
 * {@link LaunchesProjectEvent}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 * @see LaunchesProjectEvent
 *
 */
public class ProjectEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventHandler.class);

	private final ProjectEventRepository eventRepository;

	public ProjectEventHandler(ProjectEventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	/**
	 * Handle translation of a {@link MethodEvent} into the correct
	 * {@link ProjectEvent} subclass
	 * 
	 * @param methodEvent
	 *            Information from a method return annotated with
	 *            {@link LaunchesProjectEvent}
	 */
	public void delegate(MethodEvent methodEvent) {
		Class<? extends ProjectEvent> eventClass = methodEvent.getEventClass();

		ProjectEvent createdEvent = null;

		if (eventClass.equals(SampleAddedProjectEvent.class)) {
			createdEvent = handleSampleAddedProjectEvent(methodEvent);
		} else if (eventClass.equals(UserRemovedProjectEvent.class)) {
			createdEvent = handleUserRemovedEvent(methodEvent);
		} else if (eventClass.equals(UserRoleSetProjectEvent.class)) {
			createdEvent = handleUserRoleSetProjectEvent(methodEvent);
		} else {
			logger.warn("No handler found for event class " + eventClass.getName());
		}

		if (createdEvent != null) {
			eventRepository.save(createdEvent);
		}
	}

	private SampleAddedProjectEvent handleSampleAddedProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectSampleJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) method must return ProjectSampleJoin");
		}
		ProjectSampleJoin join = (ProjectSampleJoin) returnValue;
		return new SampleAddedProjectEvent(join.getSubject(), join.getObject());
	}

	private UserRemovedProjectEvent handleUserRemovedEvent(MethodEvent event) {
		Object[] args = event.getArgs();
		User user = null;
		Project project = null;
		for (Object arg : args) {
			if (arg instanceof Project) {
				project = (Project) arg;
			} else if (arg instanceof User) {
				user = (User) arg;
			}
		}
		if (user == null || project == null) {
			throw new IllegalArgumentException(
					"Project or user cannot be found on method annotated with @LaunchesProjectEvent(UserRemovedProjectEvent.class)");
		}
		return new UserRemovedProjectEvent(project, user);
	}

	private UserRoleSetProjectEvent handleUserRoleSetProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectUserJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) method must return ProjectSampleJoin");
		}
		ProjectUserJoin join = (ProjectUserJoin) returnValue;
		return new UserRoleSetProjectEvent(join.getSubject(), join.getObject(), join.getProjectRole());

	}

}
