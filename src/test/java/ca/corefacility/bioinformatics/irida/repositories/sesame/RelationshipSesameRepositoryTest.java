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

import ca.corefacility.bioinformatics.irida.utils.Identified;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RelationshipSesameRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipSesameRepositoryTest.class);
    private RelationshipSesameRepository linksRepo;
    private IdentifiedRepo repo;
    private RdfPredicate pred;
    private Identifier first;
    private Identifier second;
    private Identifier third;
    private Relationship relationship1;
    private Relationship relationship2;

    public RelationshipSesameRepositoryTest() {
    }

    @Before
    public void setUp() throws IOException {
        //Path tempDir = Files.createTempDirectory(null);
        //logger.debug("Database stored in [" + tempDir + "]");
        //SailStore store = new SailStore(tempDir);
        SailStore store = new SailStore();
        store.initialize();
        AuditRepository auditRepo = new AuditRepository(store);
        IdentifierGenerator<Identified> idGen = new IdentifierGenerator<>(store);
        IdentifierGenerator linkIdGen = new IdentifierGenerator<>(store);
        linksRepo = new RelationshipSesameRepository(store, auditRepo);
        linksRepo.setIdGen(linkIdGen);
        repo = new IdentifiedRepo(store, auditRepo, linksRepo);
        repo.setIdGen(idGen);


        pred = new RdfPredicate("irida", "identifiedRelationship");
        linksRepo.addRelationship(Identified.class, pred, Identified.class);

        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
        Identified i3 = repo.create(new Identified("third"));

        first = i1.getIdentifier();
        second = i2.getIdentifier();
        third = i3.getIdentifier();
        
        relationship1 = linksRepo.create(i1, i2);
        relationship2 = linksRepo.create(i1, i3);
        linksRepo.create(i3,i2);
    }

    /**
     * If an entity happens to be persisted without a label, then getting the entity out of the database with the entity
     * will fail because the query used to get the links out of the database relies on the existence of a label.
     */
    @Test
    public void testEmptyLinks() {
        Identified withoutLabel = repo.create(new Identified("data"));
        List<Relationship> relationships = linksRepo.getLinks(withoutLabel.getIdentifier(), Identified.class,
                Identified.class);
        assertEquals(0, relationships.size());
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
     * This method cannot be tested in this fashion due to the reliance on owl inferencing by the database
     
    @Test
    public void testGetLinks_3args_2() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        linksRepo.create(i1, i2);
        assertNotNull(linksRepo.getLinks(null, pred, null));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), null, i2.getIdentifier()));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), pred, i2.getIdentifier()));
    }*/

    /**
     * Test of addRelationship method, of class RelationshipSesameRepository.
     
    @Test
    public void testAddRelationship() {
        System.out.println("addRelationship");
        Class subject = null;
        RdfPredicate pred = null;
        Class object = null;
        RelationshipSesameRepository instance = null;
        instance.addRelationship(subject, pred, object);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of listObjects method, of class RelationshipSesameRepository.
     */
    @Test
    public void testListObjects() {
        List<Identifier> listObjects = linksRepo.listObjects(first, pred);
        assertNotNull(listObjects);
        assertTrue(listObjects.size() == 2);
    }
    
    @Test
    public void testListObjectsInvalid(){
        List<Identifier> listObjects = linksRepo.listObjects(second, pred);
        assertNotNull(listObjects);
        assertTrue(listObjects.isEmpty());
    }
    
    /**
     * Test of listSubjects method, of class RelationshipSesameRepository.
     */
    @Test
    public void testListSubjects() {
        List<Identifier> listSubjects = linksRepo.listSubjects(second, pred);
        assertNotNull(listSubjects);
        assertTrue(listSubjects.size() == 2);        
    }

    /**
     * Test of listLinks method, of class RelationshipSesameRepository.
     */
    @Test
    public void testListLinks() {
        List<Identifier> listLinks = linksRepo.listLinks(third, Identified.class, Identified.class);
        assertTrue(listLinks.size() == 1);
        Identifier get = listLinks.get(0);
        assertEquals(get.getIdentifier(),second.getIdentifier());
    }

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     * This method cannot be tested in this fashion due to the reliance on owl inferencing by the database
    @Test
    public void testGetLinks_3args_1() {
        List<Relationship> links = linksRepo.getLinks(first, Identified.class, Identified.class);
    }*/

    /**
     * Test of read method, of class RelationshipSesameRepository.
     */
    @Test
    public void testRead() {
        Relationship read = linksRepo.read(relationship1.getIdentifier());
        assertNotNull(read);
        assertEquals(read.getSubject().getIdentifier(), first.getIdentifier());
    }

    /**
     * Test of delete method, of class RelationshipSesameRepository.
     * This method cannot be tested in this fashion due to the reliance on owl inferencing by the database
    @Test
    public void testDelete_GenericType_GenericType() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
        
        Relationship create = linksRepo.create(i1, i2);
        linksRepo.delete(i1, i2);
        assertFalse(linksRepo.exists(create.getIdentifier()));
    }*/

    /**
     * Test of delete method, of class RelationshipSesameRepository.
     */
    @Test
    public void testDelete_Identifier() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
        
        Relationship create = linksRepo.create(i1, i2);
        linksRepo.delete(create.getIdentifier());
        assertFalse(linksRepo.exists(create.getIdentifier()));
    }


    /**
     * Test of exists method, of class RelationshipSesameRepository.
     */
    @Test
    public void testExists() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
        
        Relationship create = linksRepo.create(i1, i2);
        Boolean exists = linksRepo.exists(create.getIdentifier());
        assertTrue(exists);        
    }

    /**
     * Test of readMultiple method, of class RelationshipSesameRepository.
     //TODO not supported yet
    @Test
    public void testReadMultiple() {
        List<Identifier> ids = new ArrayList<>();
        ids.add(relationship1.getIdentifier());
        ids.add(relationship2.getIdentifier());
        
        Collection<Relationship> readMultiple = linksRepo.readMultiple(ids);
        assertFalse(readMultiple.isEmpty());
    }*/
}
