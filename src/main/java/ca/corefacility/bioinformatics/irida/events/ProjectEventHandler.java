package ca.corefacility.bioinformatics.irida.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserGroupRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

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
	private final ProjectRepository projectRepository;
	private final SampleRepository sampleRepository;

	public ProjectEventHandler(final ProjectEventRepository eventRepository,
			final ProjectSampleJoinRepository psjRepository, final ProjectRepository projectRepository,
			final SampleRepository sampleRepository) {
		this.eventRepository = eventRepository;
		this.psjRepository = psjRepository;
		this.projectRepository = projectRepository;
		this.sampleRepository = sampleRepository;
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
		final Date eventDate = new Date();

		Collection<ProjectEvent> events = new ArrayList<>();
		
		if (eventClass.equals(SampleAddedProjectEvent.class)) {
			events.add(handleSampleAddedProjectEvent(methodEvent));
		} else if (eventClass.equals(UserRemovedProjectEvent.class)) {
			events.add(handleUserRemovedEvent(methodEvent));
		} else if (eventClass.equals(UserRoleSetProjectEvent.class)) {
			events.add(handleUserRoleSetProjectEvent(methodEvent));
		} else if (eventClass.equals(DataAddedToSampleProjectEvent.class)) {
			final Collection<DataAddedToSampleProjectEvent> dataAddedEvents = handleSequenceFileAddedEvent(methodEvent);
			for (final DataAddedToSampleProjectEvent e : dataAddedEvents) {
				final Sample s = e.getSample();
				s.setModifiedDate(eventDate);
				sampleRepository.save(s);
			}
			events.addAll(dataAddedEvents);
		} else if (eventClass.equals(UserGroupRoleSetProjectEvent.class)) {
			events.add(handleUserGroupRoleSetProjectEvent(methodEvent));
		} else {
			logger.warn("No handler found for event class " + eventClass.getName());
		}
		
		for (ProjectEvent e : events){
			Project project = e.getProject();
			project.setModifiedDate(eventDate);
			projectRepository.save(project);
		}
	}

	/**
	 * Create a {@link SampleAddedProjectEvent}. The method must have returned a
	 * {@link ProjectSampleJoin}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private ProjectEvent handleSampleAddedProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectSampleJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) method must return ProjectSampleJoin");
		}
		ProjectSampleJoin join = (ProjectSampleJoin) returnValue;
		return eventRepository.save(new SampleAddedProjectEvent(join));
	}

	/**
	 * Create a {@link UserRemovedProjectEvent}. The method arguments must
	 * contain a {@link Project} and {@link User}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private ProjectEvent handleUserRemovedEvent(MethodEvent event) {
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
		return eventRepository.save(new UserRemovedProjectEvent(project, user));
	}

	/**
	 * Create a {@link UserRoleSetProjectEvent}. The method must have returned a
	 * {@link ProjectUserJoin}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private ProjectEvent handleUserRoleSetProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof ProjectUserJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(UserRoleSetProjectEvent.class) method must return ProjectUserJoin");
		}
		ProjectUserJoin join = (ProjectUserJoin) returnValue;
		return eventRepository.save(new UserRoleSetProjectEvent(join));

	}
	
	/**
	 * Create a {@link UserGroupRoleSetProjectEvent}. The method must have returned a
	 * {@link UserGroupProjectJoin}
	 * 
	 * @param event
	 *            The {@link MethodEvent} that this event is being launched from
	 */
	private ProjectEvent handleUserGroupRoleSetProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		if (!(returnValue instanceof UserGroupProjectJoin)) {
			throw new IllegalArgumentException(
					"Method annotated with @LaunchesProjectEvent(UserGroupRoleSetProjectEvent.class) method must return UserGroupProjectJoin");
		}
		UserGroupProjectJoin join = (UserGroupProjectJoin) returnValue;
		return eventRepository.save(new UserGroupRoleSetProjectEvent(join));

	}

	/**
	 * Create one or more {@link DataAddedToSampleProjectEvent}. Can be run on
	 * methods which return a {@link SampleSequenceFileJoin}.
	 * 
	 * @param event
	 */
	private Collection<DataAddedToSampleProjectEvent> handleSequenceFileAddedEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		Collection<DataAddedToSampleProjectEvent> events = new ArrayList<>();
		
		if (Collection.class.isAssignableFrom(returnValue.getClass())) {
			Collection<?> collection = (Collection<?>) returnValue;

			Object singleElement = collection.iterator().next();
			if (!(singleElement instanceof SampleSequenceFileJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}

			events.addAll(handleIndividualSequenceFileAddedEvent((SampleSequenceFileJoin) singleElement));
		} else {
			if (!(returnValue instanceof SampleSequenceFileJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}
			events.addAll(handleIndividualSequenceFileAddedEvent((SampleSequenceFileJoin) returnValue));
		}
		
		return events;
	}

	/**
	 * Create {@link DataAddedToSampleProjectEvent} for all {@link Project}s a
	 * {@link Sample} belongs to
	 * 
	 * @param join
	 *            a {@link SampleSequenceFileJoin} to turn into a
	 *            {@link DataAddedToSampleProjectEvent}
	 */
	private Collection<DataAddedToSampleProjectEvent> handleIndividualSequenceFileAddedEvent(SampleSequenceFileJoin join) {
		Sample subject = join.getSubject();

		Collection<DataAddedToSampleProjectEvent> events = new ArrayList<>();

		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(subject);
		for (Join<Project, Sample> psj : projectForSample) {
			events.add(eventRepository.save(new DataAddedToSampleProjectEvent(psj.getSubject(), subject)));
		}
		return events;
	}
}
