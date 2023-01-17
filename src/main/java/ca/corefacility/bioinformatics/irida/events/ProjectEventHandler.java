package ca.corefacility.bioinformatics.irida.events;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.model.enums.UserGroupRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

/**
 * Handles the creation of {@link ProjectEvent}s from methods annotated with {@link LaunchesProjectEvent}
 *
 * @see LaunchesProjectEvent
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
	 * Handle translation of a {@link MethodEvent} into the correct {@link ProjectEvent} subclass
	 *
	 * @param methodEvent Information from a method return annotated with {@link LaunchesProjectEvent}
	 */
	public void delegate(MethodEvent methodEvent) {
		Class<? extends ProjectEvent> eventClass = methodEvent.getEventClass();
		final Date eventDate = new Date();

		Collection<ProjectEvent> events = new ArrayList<>();

		if (eventClass.equals(SampleAddedProjectEvent.class)) {
			events.addAll(handleSampleAddedProjectEvent(methodEvent));
		} else if (eventClass.equals(UserRemovedProjectEvent.class)) {
			events.add(handleUserRemovedEvent(methodEvent));
		} else if (eventClass.equals(UserRoleSetProjectEvent.class)) {
			events.add(handleUserRoleSetProjectEvent(methodEvent));
		} else if (eventClass.equals(DataAddedToSampleProjectEvent.class)) {
			final Collection<DataAddedToSampleProjectEvent> dataAddedEvents = handleSequenceFileAddedEvent(methodEvent);

			/*
			 * We want the sample to show modification when these events are
			 * added, so update mod date
			 */
			for (final DataAddedToSampleProjectEvent e : dataAddedEvents) {
				sampleRepository.updateSampleModifiedDate(e.getSample(), eventDate);
			}
			events.addAll(dataAddedEvents);
		} else if (eventClass.equals(UserGroupRoleSetProjectEvent.class)) {
			events.add(handleUserGroupRoleSetProjectEvent(methodEvent));
		} else if (eventClass.equals(UserGroupRemovedProjectEvent.class)) {
			events.add(handleUserGroupRemovedEvent(methodEvent));
		} else if (eventClass.equals(SampleRemovedProjectEvent.class)) {
			events.addAll(handleSampleRemovedEvent(methodEvent));
		} else {
			logger.warn("No handler found for event class " + eventClass.getName());
		}

		// reduce projects to a distinct set, as we may have multiple events relating to the same project
		// i.e. sharing a set of samples to a project
		List<Project> projects = events.stream().map(ProjectEvent::getProject).distinct().collect(Collectors.toList());
		for (Project p : projects) {
			projectRepository.updateProjectModifiedDate(p, eventDate);
		}
	}

	/**
	 * Create one or more {@link SampleAddedProjectEvent}. Can be run on methods which return a
	 * {@link ProjectSampleJoin} or collection of joins.
	 *
	 * @param event
	 * @return A collection of newly created {@link SampleAddedProjectEvent}s
	 */
	private Collection<SampleAddedProjectEvent> handleSampleAddedProjectEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		Collection<SampleAddedProjectEvent> events = new ArrayList<>();

		if (Collection.class.isAssignableFrom(returnValue.getClass())) {
			Collection<?> collection = (Collection<?>) returnValue;

			for (Object singleElement : collection) {
				if (!(singleElement instanceof ProjectSampleJoin)) {
					throw new IllegalArgumentException(
							"Method annotated with @LaunchesProjectEvent(SampleAddedProjectEvent.class) must return one or more ProjectSampleJoin");
				}
				logger.trace("Adding multi sample " + singleElement.toString());

				events.add(eventRepository.save(new SampleAddedProjectEvent((ProjectSampleJoin) singleElement)));
			}

		} else {
			if (!(returnValue instanceof ProjectSampleJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}

			logger.trace("Adding single sample " + returnValue.toString());

			events.add(eventRepository.save(new SampleAddedProjectEvent((ProjectSampleJoin) returnValue)));
		}

		return events;
	}

	/**
	 * Create a {@link UserRemovedProjectEvent}. The method arguments must contain a {@link Project} and {@link User}
	 *
	 * @param event The {@link MethodEvent} that this event is being launched from
	 * @return The newly created {@link ProjectEvent}
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
	 * Create a {@link UserRemovedProjectEvent}. The method arguments must contain a {@link Project} and {@link User}
	 *
	 * @param event The {@link MethodEvent} that this event is being launched from
	 * @return The newly created {@link ProjectEvent}
	 */
	private ProjectEvent handleUserGroupRemovedEvent(MethodEvent event) {
		final Optional<Object> user = Arrays.stream(event.getArgs()).filter(e -> e instanceof UserGroup).findAny();
		final Optional<Object> project = Arrays.stream(event.getArgs()).filter(e -> e instanceof Project).findAny();
		if (!user.isPresent() || !project.isPresent()) {
			throw new IllegalArgumentException(
					"Project or user group cannot be found on method annotated with @LaunchesProjectEvent(UserGroupRemovedProjectEvent.class)");
		}
		return eventRepository.save(new UserGroupRemovedProjectEvent((Project) project.get(), (UserGroup) user.get()));
	}

	/**
	 * Create a {@link UserRoleSetProjectEvent}. The method must have returned a {@link ProjectUserJoin}
	 *
	 * @param event The {@link MethodEvent} that this event is being launched from
	 * @return The newly created {@link ProjectEvent}
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
	 * Create a {@link UserGroupRoleSetProjectEvent}. The method must have returned a {@link UserGroupProjectJoin}
	 *
	 * @param event The {@link MethodEvent} that this event is being launched from
	 * @return The newly created {@link ProjectEvent}
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
	 * Create one or more {@link DataAddedToSampleProjectEvent}. Can be run on methods which return a
	 * {@link SampleSequencingObjectJoin}.
	 *
	 * @param event
	 * @return a collection of newly created {@link DataAddedToSampleProjectEvent}
	 */
	private Collection<DataAddedToSampleProjectEvent> handleSequenceFileAddedEvent(MethodEvent event) {
		Object returnValue = event.getReturnValue();
		Collection<DataAddedToSampleProjectEvent> events = new ArrayList<>();

		if (Collection.class.isAssignableFrom(returnValue.getClass())) {
			Collection<?> collection = (Collection<?>) returnValue;

			Object singleElement = collection.iterator().next();
			if (!(singleElement instanceof SampleSequencingObjectJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}

			events.addAll(handleIndividualSequenceFileAddedEvent((SampleSequencingObjectJoin) singleElement));
		} else {
			if (!(returnValue instanceof SampleSequencingObjectJoin)) {
				throw new IllegalArgumentException(
						"Method annotated with @LaunchesProjectEvent(DataAddedToSampleProjectEvent.class) must return one or more SampleSequenceFileJoins");
			}
			events.addAll(handleIndividualSequenceFileAddedEvent((SampleSequencingObjectJoin) returnValue));
		}

		return events;
	}

	/**
	 * Create {@link DataAddedToSampleProjectEvent} for all {@link Project}s a {@link Sample} belongs to
	 *
	 * @param join a {@link SampleSequencingObjectJoin} to turn into a {@link DataAddedToSampleProjectEvent}
	 * @return a new collection of newly created {@link DataAddedToSampleProjectEvent}
	 */
	private Collection<DataAddedToSampleProjectEvent> handleIndividualSequenceFileAddedEvent(
			SampleSequencingObjectJoin join) {
		Sample subject = join.getSubject();

		Collection<DataAddedToSampleProjectEvent> events = new ArrayList<>();

		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(subject);
		for (Join<Project, Sample> psj : projectForSample) {
			events.add(eventRepository.save(new DataAddedToSampleProjectEvent(psj.getSubject(), subject)));
		}
		return events;
	}

	/**
	 * Create {@link SampleRemovedProjectEvent}s for any {@link Sample}s removed from a {@link Project}
	 *
	 * @param event the {@link MethodEvent} containing params from the method call
	 * @return a collectino of {@link SampleRemovedProjectEvent}s
	 */
	private Collection<SampleRemovedProjectEvent> handleSampleRemovedEvent(MethodEvent event) {
		Collection<SampleRemovedProjectEvent> events = new ArrayList<>();
		Optional<Object> projectOpt = Arrays.stream(event.getArgs()).filter(e -> e instanceof Project).findAny();
		Optional<Object> sampleOpt = Arrays.stream(event.getArgs()).filter(e -> e instanceof Sample).findAny();
		Optional<Object> sampleListOpt = Arrays.stream(event.getArgs()).filter(e -> e instanceof Iterable).findAny();

		if (projectOpt.isPresent()) {
			Project project = (Project) projectOpt.get();
			if (sampleOpt.isPresent()) {
				Sample sample = (Sample) sampleOpt.get();

				events.add(eventRepository.save(new SampleRemovedProjectEvent(project, sample.getSampleName())));
			} else if (sampleListOpt.isPresent()) {
				@SuppressWarnings("unchecked")
				Iterable<Sample> samples = (Iterable<Sample>) sampleListOpt.get();
				for (Sample sample : samples) {
					events.add(eventRepository.save(new SampleRemovedProjectEvent(project, sample.getSampleName())));
				}
			}
		}

		return events;
	}
}
