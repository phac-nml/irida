package ca.corefacility.bioinformatics.irida.events;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;

@Aspect
public class ProjectEventAspect {
	private static final Logger logger = LoggerFactory.getLogger(ProjectEventAspect.class);
	private ProjectEventRepository eventRepository;

	public ProjectEventAspect(ProjectEventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.events.SetsProjectUserRole)", returning = "projectUserJoin")
	public void processProjectUserRoleSet(JoinPoint jp, ProjectUserJoin projectUserJoin) {
		logger.debug("Creating UserRoleSetProjectEvent for " + projectUserJoin.getLabel());
		eventRepository.save(new UserRoleSetProjectEvent(projectUserJoin.getSubject(), projectUserJoin.getObject(),
				projectUserJoin.getProjectRole()));
	}

	@AfterReturning(value = "@annotation(ca.corefacility.bioinformatics.irida.events.AddsSampleToProject)", returning = "projectSampleJoin")
	public void processSampleAdded(JoinPoint jp, ProjectSampleJoin projectSampleJoin) {
		logger.debug("Creating SampleAddedProjectEvent for " + projectSampleJoin.getLabel());
		eventRepository
				.save(new SampleAddedProjectEvent(projectSampleJoin.getSubject(), projectSampleJoin.getObject()));
	}
}
