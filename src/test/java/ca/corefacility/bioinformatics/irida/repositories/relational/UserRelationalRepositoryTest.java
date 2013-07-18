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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@ContextConfiguration(locations = {"classpath:/ca/corefacility/bioinformatics/irida/config/testJdbcContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UserRelationalRepositoryTest {
    
    @Autowired
    private UserRepository repo;
    
    @Autowired
    private DataSource dataSource;
    
    @Test
    public void testCreate(){
        User user = new User("anon1", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("ROLE_USER"));
        try{
            user = repo.create(user);
            assertNotNull(user);
            assertNotNull(user.getId());
        }
        catch(IllegalArgumentException | EntityExistsException ex){
            fail();
        }
    }
    
    @Test
    public void testCreateInvalidRole(){
        User user = new User("anon2", "anon@nowhere.com", "PASSWOD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("A_FAKE_ROLE"));
        try{
            user = repo.create(user);
            fail();
        }
        catch(IllegalArgumentException ex){
        }        
    }
    
    @Test
    public void testCreateDuplicateName(){
        User user = new User("tom", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("ROLE_USER"));
        User user2 = new User("tom", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user2.setRole(new Role("ROLE_USER"));
        user2 = repo.create(user2);
        try{
            user = repo.create(user);
            fail("Should have caught duplicate username");
        }
        catch(EntityExistsException ex){
        }        
    } 
    
    @Test
    public void testGetByUsername(){
        User user = new User("tom2", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("ROLE_USER"));
        user = repo.create(user);

        try{
            User get = repo.getUserByUsername("tom2");
            assertNotNull(get);
            assertEquals(get.getUsername(),"tom2");
        }
        catch(EntityNotFoundException ex){
            fail();
        }  
    }  
    
    @Test
    public void testGetByInvalidUsername(){

        try{
            User get = repo.getUserByUsername("nobody");
            fail();
        }
        catch(EntityNotFoundException ex){
        }  
    }     
}
