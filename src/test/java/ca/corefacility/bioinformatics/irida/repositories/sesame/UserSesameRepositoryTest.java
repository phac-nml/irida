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
import ca.corefacility.bioinformatics.irida.model.User;
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
public class UserSesameRepositoryTest {
    
    private UserSesameRepository repo;
    
    @Before
    public void setUp() {
        SailMemoryStore store = new SailMemoryStore();
        repo = new UserSesameRepository(store);
        
        User u = new User("user1", "user1@there", "abc123", "user", "one", "111-111-1111");
        repo.create(u);
        
        u = new User("user2", "user2@there", "bcd234", "user", "two", "222-222-2222");
        repo.create(u);
        
        u = new User("user3", "user3@there", "cde345", "user", "three", "333-333-3333");
        repo.create(u);
        
    }

    /**
     * Test of create method, of class UserSesameRepository.
     */
    @Test
    public void testCreate() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");

        try {
            u = repo.create(u);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }
    
    @Test
    public void testAddInvalidObject() {
        User u = null;
        
        try {
            repo.create(u);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    /**
     * Test of read method, of class UserSesameRepository.
     */
    @Test
    public void testRead() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");

        u = repo.create(u);
        try{
            u = repo.read(u.getIdentifier());
            assertNotNull(u);
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
            User u = repo.read(i);
            fail();
        }
        catch(IllegalArgumentException e){
            assertNotNull(e);
        }
    }    

    /**
     * Test of getUserByUsername method, of class UserSesameRepository.
     */
    @Test
    public void testGetUserByUsername() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");

        u = repo.create(u);
        try{
            u = repo.getUserByUsername(u.getUsername());
            assertNotNull(u);
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }
    
    @Test
    public void testGetUserByUsernameInvalid() {
        try{
            User u = repo.getUserByUsername("fake");
            fail();
        }
        catch(IllegalArgumentException e){}
    }
    /**
     * Test of update method, of class UserSesameRepository.
     */
    @Test
    public void testUpdate() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");
        u = repo.create(u);
        
        try{
            u.setFirstName("different");
            u = repo.update(u);
            
            User j = repo.read(u.getIdentifier());
            assertNotNull(j);
            assertTrue(j.getFirstName().compareTo(u.getFirstName())==0);
        }
        catch(IllegalArgumentException e){
            fail();
        }
    }

    /**
     * Test of delete method, of class UserSesameRepository.
     */
    @Test
    public void testDelete() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");
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
     * Test of list method, of class UserSesameRepository.
     */
    @Test
    public void testList_0args() {
        List<User> users = repo.list();
        if(users.isEmpty()){
            fail();
        }
    }

    /**
     * Test of list method, of class UserSesameRepository.
     */
    @Test
    public void testList_4args() {
        List<User> users = repo.list(0, 1, null, Order.ASCENDING);
        
        if(users.size() != 1){
            fail();
        }
    }

    /**
     * Test of exists method, of class UserSesameRepository.
     */
    @Test
    public void testExists() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");
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
     * Test of checkUsernameExists method, of class UserSesameRepository.
     */
    @Test
    public void testCheckUsernameExists() {
        User u = new User("test", "testuser@test", "123456", "a", "test", "123-456-7890");
        u = repo.create(u);
        
        try{
            if(! repo.checkUsernameExists(u.getUsername())){
                fail();
            }
        }
        catch(IllegalArgumentException e){
            fail();
        } 
    }

}