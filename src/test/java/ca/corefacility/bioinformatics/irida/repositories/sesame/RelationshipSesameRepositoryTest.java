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

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailMemoryStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RelationshipSesameRepositoryTest {

    RelationshipSesameRepository linksRepo;
    IdentifiedRepo repo;
    RdfPredicate pred;

    public RelationshipSesameRepositoryTest() {
    }

    @Before
    public void setUp() {
        SailMemoryStore store = new SailMemoryStore();
        store.initialize();
        AuditRepository auditRepo = new AuditRepository(store);
        linksRepo = new RelationshipSesameRepository(store, auditRepo);
        repo = new IdentifiedRepo(store, auditRepo, linksRepo);


        pred = new RdfPredicate("irida", "identifiedRelationship");
        linksRepo.addRelationship(Identified.class, pred, Identified.class);

        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        linksRepo.create(i1, i2);
    }

    @Test
    public void testGetLinks() {

    }

    /**
     * Test of create method, of class RelationshipSesameRepository.
     */
    @Test
    public void testCreate_GenericType_GenericType() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        Relationship create = linksRepo.create(i1, i2);
        assertNotNull(create);
        assertNotNull(create.getIdentifier());
    }

    /**
     * Test of create method, of class RelationshipSesameRepository.
     */
    @Test
    public void testCreate_Relationship() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        Relationship r = new Relationship(i1.getIdentifier(), pred, i2.getIdentifier());

        Relationship create = linksRepo.create(r);
        assertNotNull(create);
        assertNotNull(create.getIdentifier());
    }

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     */
    @Test
    public void testGetLinks_3args_2() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        linksRepo.create(i1, i2);
        assertNotNull(linksRepo.getLinks(null, pred, null));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), null, i2.getIdentifier()));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), pred, i2.getIdentifier()));
    }
}