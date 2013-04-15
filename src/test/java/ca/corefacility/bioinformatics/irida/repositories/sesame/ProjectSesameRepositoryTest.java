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

import ca.corefacility.bioinformatics.irida.dao.SailMemoryStore;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.net.URI;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectSesameRepositoryTest {
    
    private ProjectSesameRepository repo;
    
    @Before
    public void setUp() {
        SailMemoryStore store = new SailMemoryStore();
        repo = new ProjectSesameRepository(store);
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

    /**
     * Test of create method, of class ProjectSesameRepository.
     */
    @Test
    public void testAddValidObject() {
        Project p = new Project();
        p.setName("new project");
        
        try {
            repo.create(p);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }
    @Test
    public void testAddInvalidObject() {
        Project p = null;
        
        try {
            repo.create(p);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test of read method, of class ProjectSesameRepository.
     */
    @Test
    public void testRead() {
        Project p = new Project();
        p.setName("new project");
        p = repo.create(p);
        try{
            p = repo.read(p.getIdentifier());
            assertNotNull(p);
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
            Project p = repo.read(i);
            fail();
        }
        catch(IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    /**
     * Test of update method, of class ProjectSesameRepository.
     */
    @Test
    public void testUpdate() {
        Project p = new Project();
        p.setName("new project");
        p = repo.create(p);
        
        p.setName("different name");
        p = repo.update(p);
        
        try{
            Project j = repo.read(p.getIdentifier());
            assertNotNull(j);
            assertTrue(j.getName().compareTo(p.getName())==0);
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }

    /**
     * Test of delete method, of class ProjectSesameRepository.
     */
    @Test
    public void testDelete() {
        Project p = new Project();
        p.setName("new project");
        p = repo.create(p);
        
        try{
            repo.delete(p.getIdentifier());
            
            if(repo.exists(p.getIdentifier())){
                fail();
            }
        }
        catch(IllegalArgumentException e){
            fail();
        }
        
    }
    
    @Test
    public void testDeleteInvalid() {
        Identifier inv = new Identifier();
        inv.setUri(java.net.URI.create("http://nowhere/FFFFFFFF"));
        
        try{
            repo.delete(inv);
            
            fail();
        }
        catch(IllegalArgumentException e){}
        
    }    

    /**
     * Test of list method, of class ProjectSesameRepository.
     */
    @Test
    public void testList_0args() {
        List<Project> projects = repo.list();
        if(projects.isEmpty()){
            fail();
        }
    }

    /**
     * Test of list method, of class ProjectSesameRepository.
     */
    @Test
    public void testList_4args() {
        List<Project> projects = repo.list(0, 1, null, Order.ASCENDING);
        
        if(projects.size() != 1){
            fail();
        }
    }

    /**
     * Test of exists method, of class ProjectSesameRepository.
     */
    @Test
    public void testExists() {
        Project p = new Project();
        p.setName("new project");
        p = repo.create(p);
        
        try{
            if(! repo.exists(p.getIdentifier())){
                fail();
            }
        }
        catch(IllegalArgumentException e){
            fail();
        }        
    }

}