package ca.corefacility.bioinformatics.irida.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.AddsSampleToProject;
import ca.corefacility.bioinformatics.irida.events.annotations.RemovesUserFromProject;
import ca.corefacility.bioinformatics.irida.events.annotations.SetsProjectUserRole;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;

/**
 * Aspect used to create project events for methods annotated with event
 * annotations
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @see SetsProjectUserRole
 * @see AddsSampleToProject
 * @see RemovesUserFromProject
 */
@Aspect
public class ProjectEventAspect {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventAspect.class);
	private ProjectEventRepository eventRepository;

	public ProjectEventAspect(ProjectEventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	/**
	 * Create a {@link UserRoleSetProjectEvent} for a
	 * {@link SetsProjectUserRole} annotated method
	 * 
	 * @param projectUserJoin
	 *            the {@link ProjectUserJoin} created by the method
	 */
	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.events.annotations.SetsProjectUserRole)", returning = "projectUserJoin")
	public void processProjectUserRoleSet(ProjectUserJoin projectUserJoin) {
		logger.debug("Creating UserRoleSetProjectEvent for " + projectUserJoin.getLabel());
		eventRepository.save(new UserRoleSetProjectEvent(projectUserJoin.getSubject(), projectUserJoin.getObject(),
				projectUserJoin.getProjectRole()));
	}

	/**
	 * Create a {@link SampleAddedProjectEvent} for a method annotated with
	 * {@link AddsSampleToProject}
	 * 
	 * @param projectSampleJoin
	 *            the {@link ProjectSampleJoin} created by the method
	 */
	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.events.annotations.AddsSampleToProject)", returning = "projectSampleJoin")
	public void processSampleAdded(ProjectSampleJoin projectSampleJoin) {
		logger.debug("Creating SampleAddedProjectEvent for " + projectSampleJoin.getLabel());
		eventRepository
				.save(new SampleAddedProjectEvent(projectSampleJoin.getSubject(), projectSampleJoin.getObject()));
	}

	/**
	 * Create a {@link UserRemovedProjectEvent} for a method annotated with
	 * {@link RemovesUserFromProject}. Methods annotated with this must have a
	 * {@link Project} and {@link User} argument.
	 * 
	 * @param jp
	 *            JoinPoint from the pointcut
	 */
	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.events.annotations.RemovesUserFromProject)")
	public void processUserRemoved(JoinPoint jp) {
		Object[] args = jp.getArgs();
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
					"Project or user cannot be found on method annotated with @RemovesUserFromProject");
		}
		logger.debug("Removing user " + user.getLabel() + " from project " + project.getLabel());
		eventRepository.save(new UserRemovedProjectEvent(project, user));
	}
}
