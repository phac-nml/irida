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
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import ca.corefacility.bioinformatics.irida.model.Project;

import org.junit.Before;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectSesameRepositoryTest {
    
    private ProjectSesameRepository repo;
    
    @Before
    public void setUp() {
        SailStore store = new SailStore();
        store.initialize();
        AuditRepository auditRepo = new AuditRepository(store);
        RelationshipSesameRepository linksRepo = new RelationshipSesameRepository(store, auditRepo);
        repo = new ProjectSesameRepository(store,auditRepo,linksRepo);
        Project p = new Project();
        p.setName("p1");
        repo.create(p);
        p = new Project();
        p.setName("p2");
        repo.create(p);
        p = new Project();
        p.setName("p3");
        repo.create(p);
        
    }

}