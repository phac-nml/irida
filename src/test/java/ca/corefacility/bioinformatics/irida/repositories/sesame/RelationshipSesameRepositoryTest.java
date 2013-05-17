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
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailMemoryStore;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;

/**
 *
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
        repo = new IdentifiedRepo(store,auditRepo,linksRepo);

        
        pred = new RdfPredicate("irida", "identifiedRelationship");
        linksRepo.addRelationship(Identified.class, pred, Identified.class);
        
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
                
        linksRepo.create(i1,i2);
    }

    /**
     * Test of buildIdentiferFromBindingSet method, of class RelationshipSesameRepository.
     
    @Test
    public void testBuildIdentiferFromBindingSet() {
        System.out.println("buildIdentiferFromBindingSet");
        BindingSet bs = null;
        String bindingName = "";
        RelationshipSesameRepository instance = null;
        StringIdentifier expResult = null;
        StringIdentifier result = instance.buildIdentiferFromBindingSet(bs, bindingName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of buildLinkIdentifier method, of class RelationshipSesameRepository.
     
    @Test
    public void testBuildLinkIdentifier() {
        System.out.println("buildLinkIdentifier");
        URI uri = null;
        String identifiedBy = "";
        RelationshipSesameRepository instance = null;
        Identifier expResult = null;
        Identifier result = instance.buildLinkIdentifier(uri, identifiedBy);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of create method, of class RelationshipSesameRepository.
     */
    @Test
    public void testCreate_GenericType_GenericType() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
                
        Relationship create = linksRepo.create(i1,i2);
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
     * Test of listObjects method, of class RelationshipSesameRepository.
     
    @Test
    public void testListObjects() {
        System.out.println("listObjects");
        Identifier subjectId = null;
        RdfPredicate predicate = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.listObjects(subjectId, predicate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of listSubjects method, of class RelationshipSesameRepository.
     
    @Test
    public void testListSubjects() {
        System.out.println("listSubjects");
        Identifier objectId = null;
        RdfPredicate predicate = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.listSubjects(objectId, predicate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of listLinks method, of class RelationshipSesameRepository.
     
    @Test
    public void testListLinks() {
        System.out.println("listLinks");
        Identifier id = null;
        Class subjectType = null;
        Class objectType = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.listLinks(id, subjectType, objectType);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     
    @Test
    public void testGetLinks_3args_1() {
        System.out.println("getLinks");
        Identifier subjectId = null;
        Class subjectType = null;
        Class objectType = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.getLinks(subjectId, subjectType, objectType);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     
    @Test
    public void testGetLinks_Identifier_RdfPredicate() {
        System.out.println("getLinks");
        Identifier subjectId = null;
        RdfPredicate predicate = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.getLinks(subjectId, predicate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     */
    @Test
    public void testGetLinks_3args_2() {
        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));
                
        linksRepo.create(i1,i2);
        assertNotNull(linksRepo.getLinks(null, pred, null));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), null, i2.getIdentifier()));
        assertNotNull(linksRepo.getLinks(i1.getIdentifier(), pred, i2.getIdentifier()));
    }

    /**
     * Test of getSubjectLinks method, of class RelationshipSesameRepository.
     
    @Test
    public void testGetSubjectLinks() {
        System.out.println("getSubjectLinks");
        Identifier objectId = null;
        RdfPredicate predicate = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.getSubjectLinks(objectId, predicate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getLinks method, of class RelationshipSesameRepository.
     
    @Test
    public void testGetLinks_Identifier() {
        System.out.println("getLinks");
        Identifier subjectId = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.getLinks(subjectId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of read method, of class RelationshipSesameRepository.
     
    @Test
    public void testRead() {
        System.out.println("read");
        Identifier id = null;
        RelationshipSesameRepository instance = null;
        Relationship expResult = null;
        Relationship result = instance.read(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of update method, of class RelationshipSesameRepository.
     
    @Test
    public void testUpdate() {
        System.out.println("update");
        Relationship object = null;
        RelationshipSesameRepository instance = null;
        Relationship expResult = null;
        Relationship result = instance.update(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of delete method, of class RelationshipSesameRepository.
     
    @Test
    public void testDelete() {
        System.out.println("delete");
        Identifier id = null;
        RelationshipSesameRepository instance = null;
        instance.delete(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of list method, of class RelationshipSesameRepository.
     
    @Test
    public void testList_0args() {
        System.out.println("list");
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.list();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of list method, of class RelationshipSesameRepository.
     
    @Test
    public void testList_4args() {
        System.out.println("list");
        int page = 0;
        int size = 0;
        String sortProperty = "";
        Order order = null;
        RelationshipSesameRepository instance = null;
        List expResult = null;
        List result = instance.list(page, size, sortProperty, order);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of exists method, of class RelationshipSesameRepository.
     
    @Test
    public void testExists() {
        System.out.println("exists");
        Identifier id = null;
        RelationshipSesameRepository instance = null;
        Boolean expResult = null;
        Boolean result = instance.exists(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of count method, of class RelationshipSesameRepository.
     
    @Test
    public void testCount() {
        System.out.println("count");
        RelationshipSesameRepository instance = null;
        Integer expResult = null;
        Integer result = instance.count();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}