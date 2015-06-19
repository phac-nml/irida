package ca.corefacility.bioinformatics.irida.events;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;

/**
 * Handles the creation of {@link ProjectEvent}s from methods annotated with
 * {@link LaunchesProjectEvent}
 * 
 * 
 * @see LaunchesProjectEvent
 *
 */
public class ProjectEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventHandler.class);

	private final ProjectEventRepository eventRepository;
	private final ProjectSampleJoinRepository psjRepository;

	public ProjectEventHandler(ProjectEventRepository eventRepository, ProjectSampleJoinRepository psjRepository) {
		this.eventRepository = eventRepository;
		this.psjRepository = psjRepository;
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

		if (eventClass.equals(SampleAddedProjectEvent.class)) {
			handleSampleAddedProjectEvent(methodEvent);
		} else if (eventClass.equals(UserRemovedProjectEvent.class)) {
			handleUserRemovedEvent(methodEvent);
		} else if (eventClass.equals(UserRoleSetProjectEvent.class)) {
			handleUserRoleSetProjectEvent(methodEvent);
		} else if (eventClass.equals(DataAddedToSampleProjectEvent.class)) {
			handleSequenceFileAddedEvent(methodEvent);
		} else {
			logger.warn("No handler found for event class " + eventClass.getName());
		}
	}

	/**
	 * Create a {@link SampleAddedProjectEvent}. The method must have returned a
	 * {@link ProjectSampleJoin}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private void handleSampleAddedProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectSampleJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) method must return ProjectSampleJoin");
		}
		ProjectSampleJoin join = (ProjectSampleJoin) returnValue;
		eventRepository.save(new SampleAddedProjectEvent(join));
	}

	/**
	 * Create a {@link UserRemovedProjectEvent}. The method arguments must
	 * contain a {@link Project} and {@link User}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private void handleUserRemovedEvent(MethodEvent event) {
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
		eventRepository.save(new UserRemovedProjectEvent(project, user));
	}

	/**
	 * Create a {@link UserRoleSetProjectEvent}. The method must have returned a
	 * {@link ProjectUserJoin}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private void handleUserRoleSetProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectUserJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) method must return ProjectSampleJoin");
		}
		ProjectUserJoin join = (ProjectUserJoin) returnValue;
		eventRepository.save(new UserRoleSetProjectEvent(join));

	}

	/**
	 * Create one or more {@link DataAddedToSampleProjectEvent}. Can be run on
	 * methods which return a {@link SampleSequenceFileJoin}.
	 * 
	 * @param event
	 */
	private void handleSequenceFileAddedEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (Collection.class.isAssignableFrom(returnValue.getClass())) {
			Collection<?> collection = (Collection<?>) returnValue;

			Object singleElement = collection.iterator().next();
			if (!(singleElement instanceof SampleSequenceFileJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}

			handleIndividualSequenceFileAddedEvent((SampleSequenceFileJoin) singleElement);
		} else {
			if (!(returnValue instanceof SampleSequenceFileJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}
			handleIndividualSequenceFileAddedEvent((SampleSequenceFileJoin) returnValue);
		}
	}

	/**
	 * Create {@link DataAddedToSampleProjectEvent} for all {@link Project}s a
	 * {@link Sample} belongs to
	 * 
	 * @param join
	 *            a {@link SampleSequenceFileJoin} to turn into a
	 *            {@link DataAddedToSampleProjectEvent}
	 */
	private void handleIndividualSequenceFileAddedEvent(SampleSequenceFileJoin join) {
		Sample subject = join.getSubject();

		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(subject);
		for (Join<Project, Sample> psj : projectForSample) {
			eventRepository.save(new DataAddedToSampleProjectEvent(psj.getSubject(), subject));
		}

	}
}
