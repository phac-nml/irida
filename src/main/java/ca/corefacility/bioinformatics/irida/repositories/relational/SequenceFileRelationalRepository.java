package ca.corefacility.bioinformatics.irida.repositories.relational;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

/**
 * A repository for storing information about a {@link SequenceFile} in a relational database.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Repository
@Transactional
public class SequenceFileRelationalRepository extends GenericRelationalRepository<SequenceFile> implements SequenceFileRepository{
    public SequenceFileRelationalRepository(){}
    
    public SequenceFileRelationalRepository(DataSource source, SessionFactory sessionFactory){
        super(source, sessionFactory, SequenceFile.class);
    }

    @Override
    protected SequenceFile postLoad(SequenceFile object) {
        object.setRealPath();
        return object;
    }
    
    @Override
    protected SequenceFile preSave(SequenceFile object){
        object.setStringPath();
        return object;
    }
    
    /**
     * {@inheritDoc }
     * @deprecated 
     */
    @Override
    public List<ProjectSequenceFileJoin> getFilesForProject(Project project) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(ProjectSequenceFileJoin.class);
        crit.add(Restrictions.eq("project", project));
        crit.createCriteria("sequenceFile").add(Restrictions.eq("enabled", true));
        @SuppressWarnings("unchecked")
		List<ProjectSequenceFileJoin> list = crit.list();
        
        return list;        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<SampleSequenceFileJoin> getFilesForSample(Sample sample) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(SampleSequenceFileJoin.class);
        crit.add(Restrictions.eq("sample", sample));
        crit.createCriteria("sequenceFile").add(Restrictions.eq("enabled", true));
        @SuppressWarnings("unchecked")
		List<SampleSequenceFileJoin> list = crit.list();
        
        for(SampleSequenceFileJoin join : list){
            join.getObject().setRealPath();
        }
        
        return list;
    }
    
    /**
     * {@inheritDoc }
     * @deprecated 
     */
    @Override
    public ProjectSequenceFileJoin addFileToProject(Project project, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();

        ProjectSequenceFileJoin ujoin = new ProjectSequenceFileJoin(project, file);
        session.persist(ujoin);
        
        return ujoin;    
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public SampleSequenceFileJoin addFileToSample(Sample sample, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();

        SampleSequenceFileJoin ujoin = new SampleSequenceFileJoin(sample, file);
        session.persist(ujoin);
        
        return ujoin;
    }
    
    /**
     * {@inheritDoc }
     * @deprecated 
     */
    @Override
    public void removeFileFromProject(Project project, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(ProjectSequenceFileJoin.class);
        crit.add(Restrictions.eq("project", project));
        crit.add(Restrictions.eq("sequenceFile", file));
        
        ProjectSequenceFileJoin join = (ProjectSequenceFileJoin) crit.uniqueResult();
        if(join == null){
            throw new EntityNotFoundException("A join between this file and project was not found");
        }
        session.delete(join);    
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFileFromSample(Sample sample, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(SampleSequenceFileJoin.class);
        crit.add(Restrictions.eq("sample", sample));
        crit.add(Restrictions.eq("sequenceFile", file));
        
        SampleSequenceFileJoin join = (SampleSequenceFileJoin) crit.uniqueResult();
        if(join == null){
            throw new EntityNotFoundException("A join between this file and sample was not found");
        }
        session.delete(join);     
    }
    
    public List<MiseqRunSequenceFileJoin> getFilesForMiseqRun(MiseqRun run){
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(MiseqRunSequenceFileJoin.class);
        crit.add(Restrictions.eq("miseqRun", run));
        
        List<MiseqRunSequenceFileJoin> list = crit.list();
        for(MiseqRunSequenceFileJoin join : list){
            join.getObject().setRealPath();
        }
        
        return list;

    }
    
}
