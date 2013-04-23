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

import ca.corefacility.bioinformatics.irida.dao.PropertyMapper;
import ca.corefacility.bioinformatics.irida.dao.SailMemoryStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openrdf.query.BindingSet;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class GenericRepositoryTest {
    
    GenericRepository<Identifier,Identified> repo;
    
    public GenericRepositoryTest() {
    }
    
    @Before
    public void setUp() {
        SailMemoryStore store = new SailMemoryStore();
        store.initialize();
        
        repo = new GenericRepository<>(store, Identified.class);
        PropertyMapper map = new PropertyMapper("irida", "Identified");
        try {
            map.addProperty("irida", "data", "data", Identified.class.getMethod("getData"), Identified.class.getMethod("setData", String.class), String.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(GenericRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        repo.setPropertyMap(map);
        
        repo.create(new Identified("data1"));
        repo.create(new Identified("data2"));
        repo.create(new Identified("data3"));
    }
    
    /**
     * Test of generateIdentifier method, of class GenericRepository.
     
    @Test
    public void testGenerateIdentifier() {
        System.out.println("generateIdentifier");
        Object t = null;
        GenericRepository instance = new GenericRepository();
        Identifier expResult = null;
        Identifier result = instance.generateIdentifier(t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of buildIdentifier method, of class GenericRepository.
     
    @Test
    public void testBuildIdentifier() {
        System.out.println("buildIdentifier");
        BindingSet bs = null;
        String subject = "";
        GenericRepository instance = new GenericRepository();
        Identifier expResult = null;
        Identifier result = instance.buildIdentifier(bs, subject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getPropertyMap method, of class GenericRepository.
     
    @Test
    public void testGetPropertyMap() {
        System.out.println("getPropertyMap");
        GenericRepository instance = new GenericRepository();
        PropertyMapper expResult = null;
        PropertyMapper result = instance.getPropertyMap();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of buildURI method, of class GenericRepository.
     
    @Test
    public void testBuildURI() {
        System.out.println("buildURI");
        String id = "";
        GenericRepository instance = new GenericRepository();
        URI expResult = null;
        URI result = instance.buildURI(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of setPropertyMap method, of class GenericRepository.
     
    @Test
    public void testSetPropertyMap() {
        System.out.println("setPropertyMap");
        PropertyMapper propertyMap = null;
        GenericRepository instance = new GenericRepository();
        instance.setPropertyMap(propertyMap);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of create method, of class GenericRepository.
     */
    @Test
    public void testCreate() {
        Identified i = new Identified("newdata");

        try {
            i = repo.create(i);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }
    
    @Test
    public void testAddInvalidObject() {
        Identified i = null;
        
        try {
            repo.create(i);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }    

    /**
     * Test of read method, of class GenericRepository.
     */
    @Test
    public void testRead() {
        Identified i = new Identified("newdata");

        i = repo.create(i);
        try{
            i = repo.read(i.getIdentifier());
            assertNotNull(i);
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }
    
    @Test
    public void testReadInvalid() {
        try{
            Identifier i = new Identifier();
            i.setUri(URI.create("http://nowhere/fake"));
            Identified u = repo.read(i);
            fail();
        }
        catch(EntityNotFoundException e){
            assertNotNull(e);
        }
    } 
    
    /**
     * Test of list method, of class GenericRepository.
     */
    @Test
    public void testList_0args() {
        List<Identified> users = repo.list();
        if(users.isEmpty()){
            fail();
        }
    }

    /**
     * Test of list method, of class GenericRepository.
     */
    @Test
    public void testList_4args() {
        List<Identified> users = repo.list(0, 1, null, Order.ASCENDING);
        
        if(users.size() != 1){
            fail();
        }
    }

    /**
     * Test of delete method, of class GenericRepository.
     */
    @Test
    public void testDelete() {
        Identified u = new Identified("newdata");
        u = repo.create(u);
        
        try{
            repo.delete(u.getIdentifier());
            if(repo.exists(u.getIdentifier())){
                fail();
            }
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }
    
    @Test
    public void testDeleteInvalid() {
        Identifier i = new Identifier();
        i.setUri(URI.create("http://nowhere/fake"));
        
        try{
            repo.delete(i);
            fail();
        }
        catch(IllegalArgumentException e){}
    }    
    /**
     * Test of exists method, of class GenericRepository.
     */
    @Test
    public void testExists() {
        Identified u = new Identified("newdata");
        u = repo.create(u);
        
        try{
            if(! repo.exists(u.getIdentifier())){
                fail();
            }
        }
        catch(IllegalArgumentException e){
            fail();
        } 
    }

    /**
     * Test of buildParams method, of class GenericRepository.
     
    @Test
    public void testBuildParams() {
        System.out.println("buildParams");
        String subject = "";
        PropertyMapper map = null;
        GenericRepository instance = new GenericRepository();
        String expResult = "";
        String result = instance.buildParams(subject, map);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of extractData method, of class GenericRepository.
     
    @Test
    public void testExtractData() {
        System.out.println("extractData");
        Identifier id = null;
        BindingSet bindingSet = null;
        GenericRepository instance = new GenericRepository();
        Object expResult = null;
        Object result = instance.extractData(id, bindingSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of update method, of class GenericRepository.
     */
    @Test
    public void testUpdate() {
        Identified u = new Identified("newdata");
        u = repo.create(u);
        
        try{
            u.setData("different");
            u = repo.update(u);
            
            Identified j = repo.read(u.getIdentifier());
            assertNotNull(j);
            assertTrue(j.getData().compareTo(u.getData())==0);
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }

    /**
     * Test of count method, of class GenericRepository.
     */
    @Test
    public void testCount() {
        repo.create(new Identified("newdata"));
        
        assertTrue(repo.count()> 0);
    }

}