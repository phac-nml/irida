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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
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
public class SampleRelationalRepository extends GenericRelationalRepository<Sample> implements SampleRepository{

    public SampleRelationalRepository(){}
    
    public SampleRelationalRepository(DataSource source){
        super(source, Sample.class);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<ProjectSampleJoin> getSamplesForProject(Project project) {
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ProjectSampleJoin.class);
        crit.add(Restrictions.eq("project", project));
        crit.createCriteria("sample").add(Restrictions.eq("enabled", true));
        List<ProjectSampleJoin> list = crit.list();
        
        return list;   
    }
    
}
