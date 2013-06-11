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

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openrdf.model.URI;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserIdentifierGeneratorTest {
    UserIdentifierGenerator idGen;

    @Before
    public void setUp() {
        SailStore store = new SailStore();
        idGen = new UserIdentifierGenerator(store);
    }

    /**
     * Test of generateNewIdentifier method, of class UserIdentifierGenerator.
     */
    @Test
    public void testGenerateNewIdentifier() {
        User u = new User("john", "john@nowhere.com", "PASSWOD!1", "John", "Doe", "1234");
        try{
            Identifier id = idGen.generateNewIdentifier(u, "http://nowhere");
            assertNotNull(id);
            assertEquals(id.getLabel(),u.getLabel());
            assertNotNull(id.getUri());
        }
        catch(IllegalArgumentException ex){
            fail();
        }
    }
    
    @Test
    public void testGenerateInvalid(){
        try{
            Identifier id = idGen.generateNewIdentifier(null, "http://nowhere");    
            fail();
        }
        catch(IllegalArgumentException ex){
        }
        
        
    }

    /**
     * Test of buildIdentifier method, of class UserIdentifierGenerator.
     
    @Test
    public void testBuildIdentifier() {
        System.out.println("buildIdentifier");
        User object = null;
        URI uri = null;
        String identifiedBy = "";
        UserIdentifierGenerator instance = null;
        Identifier expResult = null;
        Identifier result = instance.buildIdentifier(object, uri, identifiedBy);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}