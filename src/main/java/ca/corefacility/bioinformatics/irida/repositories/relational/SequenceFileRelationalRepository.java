/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileProjectJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileSampleJoin;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Repository
@Transactional
public class SequenceFileRelationalRepository extends GenericRelationalRepository<SequenceFile> implements SequenceFileRepository{
    public SequenceFileRelationalRepository(){}
    
    public SequenceFileRelationalRepository(DataSource source){
        super(source,SequenceFile.class);
    }

    @Transactional
    @Override
    public SequenceFile create(SequenceFile object) throws IllegalArgumentException {
        object.setStringPath(); //we need to make sure the string path is populated here.  a bit of a pain
        
        return super.create(object);
    }

    @Override
    protected void postLoad(SequenceFile object) {
        object.setRealPath();
    }

    @Override
    public List<SequenceFileProjectJoin> getFilesForProject(Project project) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(SequenceFileProjectJoin.class);
        crit.add(Restrictions.eq("project", project));
        List<SequenceFileProjectJoin> list = crit.list();
        
        return list;        
    }

    @Override
    public List<SequenceFileSampleJoin> getFilesForSample(Sample sample) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(SequenceFileSampleJoin.class);
        crit.add(Restrictions.eq("sample", sample));
        List<SequenceFileSampleJoin> list = crit.list();
        
        return list;
    }

    @Override
    public SequenceFileProjectJoin addFileToProject(Project project, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();

        SequenceFileProjectJoin ujoin = new SequenceFileProjectJoin(file, project);
        session.save(ujoin);
        
        return ujoin;    
    }

    @Override
    public SequenceFileSampleJoin addFileToSample(Sample sample, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();

        SequenceFileSampleJoin ujoin = new SequenceFileSampleJoin(file, sample);
        session.save(ujoin);
        
        return ujoin;
    }

    @Override
    public void removeFileFromProject(Project project, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(SequenceFileProjectJoin.class);
        crit.add(Restrictions.eq("project", project));
        crit.add(Restrictions.eq("sequenceFile", file));
        
        SequenceFileProjectJoin join = (SequenceFileProjectJoin) crit.uniqueResult();
        if(join == null){
            throw new EntityNotFoundException("A join between this file and project was not found");
        }
        session.delete(join);    
    }
    
}
