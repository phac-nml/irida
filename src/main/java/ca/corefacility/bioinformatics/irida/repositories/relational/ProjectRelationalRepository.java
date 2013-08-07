package ca.corefacility.bioinformatics.irida.repositories.relational;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Transactional
@Repository
public class ProjectRelationalRepository extends GenericRelationalRepository<Project> implements ProjectRepository {

	public ProjectRelationalRepository() {
	}

	public ProjectRelationalRepository(DataSource source) {
		super(source, Project.class);
	}

	/**
	 * {@inheritDoc }
	 */
	@Transactional
	@Override
	public Collection<ProjectUserJoin> getProjectsForUser(User user) {
		Session session = sessionFactory.getCurrentSession();

		Criteria crit = session.createCriteria(ProjectUserJoin.class);
		crit.add(Restrictions.eq("user", user));
		crit.createCriteria("project").add(Restrictions.eq("enabled", true));
		@SuppressWarnings("unchecked")
		List<ProjectUserJoin> list = crit.list();

		return list;
	}

	/**
	 * {@inheritDoc }
	 */
	@Transactional
	@Override
	public ProjectUserJoin addUserToProject(Project project, User user) {
		Session session = sessionFactory.getCurrentSession();

		Criteria query = session.createCriteria(ProjectUserJoin.class).add(Restrictions.eq("user", user))
				.add(Restrictions.eq("project", project));

		@SuppressWarnings("unchecked")
		List<ProjectUserJoin> list = query.list();
		if (!list.isEmpty()) {
			throw new EntityExistsException("This user already belongs to this project");
		}

		ProjectUserJoin ujoin = new ProjectUserJoin(project, user);
		session.persist(ujoin);

		return ujoin;
	}

	/**
	 * {@inheritDoc }
	 */
	@Transactional
	@Override
	public void removeUserFromProject(Project project, User user) {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(ProjectUserJoin.class);
		crit.add(Restrictions.eq("project", project));
		crit.add(Restrictions.eq("user", user));

		ProjectUserJoin join = (ProjectUserJoin) crit.uniqueResult();
		if (join == null) {
			throw new EntityNotFoundException("A join between this user and project was not found");
		}
		session.delete(join);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ProjectSampleJoin addSampleToProject(Project project, Sample sample) {
		Session session = sessionFactory.getCurrentSession();

		ProjectSampleJoin ujoin = new ProjectSampleJoin(project, sample);
		session.persist(ujoin);

		return ujoin;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ProjectSampleJoin getProjectForSample(Sample sample) {
		Session session = sessionFactory.getCurrentSession();

		Criteria crit = session.createCriteria(ProjectSampleJoin.class);
		crit.add(Restrictions.eq("sample", sample));
		crit.createCriteria("project").add(Restrictions.eq("enabled", true));

		return (ProjectSampleJoin) crit.uniqueResult();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void removeSampleFromProject(Project project, Sample sample) throws EntityNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(ProjectSampleJoin.class);
		crit.add(Restrictions.eq("project", project));
		crit.add(Restrictions.eq("sample", sample));

		ProjectSampleJoin join = (ProjectSampleJoin) crit.uniqueResult();
		if (join == null) {
			throw new EntityNotFoundException("A join between this sample and project was not found");
		}
		session.delete(join);
	}

	/**
	 * {@inheritDoc }
	 * 
	 * @deprecated
	 */
	@Override
	public void removeFileFromProject(Project project, SequenceFile file) {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(ProjectSequenceFileJoin.class);
		crit.add(Restrictions.eq("project", project));
		crit.add(Restrictions.eq("sequenceFile", file));

		ProjectSequenceFileJoin join = (ProjectSequenceFileJoin) crit.uniqueResult();
		if (join == null) {
			throw new EntityNotFoundException("A join between this file and project was not found");
		}
		session.delete(join);
	}

}
