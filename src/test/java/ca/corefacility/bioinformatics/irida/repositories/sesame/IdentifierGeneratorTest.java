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

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import ca.corefacility.bioinformatics.irida.utils.Identified;
import java.net.URI;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class IdentifierGeneratorTest {
    IdentifierGenerator idGen;
    IdentifiedRepo repo;
    public IdentifierGeneratorTest() {
    }
    
    @Before
    public void setUp() throws NoSuchMethodException {
        SailStore store = new SailStore();
        store.initialize();
        idGen = new IdentifierGenerator<>(store);
        AuditRepository arepo = new AuditRepository(store);
        RelationshipSesameRepository relRepo = new RelationshipSesameRepository(store, arepo);
        repo = new IdentifiedRepo(store, arepo, relRepo);
        repo.setIdGen(idGen);
    }    

    /**
     * Test of generateNewIdentifier method, of class IdentifierGenerator.
     */
    @Test
    public void testGenerateNewIdentifier() {
        Identified obj = new Identified("bleh","newudata");
        Identifier id = idGen.generateNewIdentifier(obj, "http://nowhere");
        assertNotNull(id);
        assertNotNull(id.getIdentifier());
        assertNotNull(id.getUri());
    }

    /**
     * Test of buildURIFromIdentifiedBy method, of class IdentifierGenerator.
     */
    @Test
    public void testBuildURIFromIdentifiedBy() {
        String identifed = "identified";
        String baseURI = "http://nowhere";
        URI uri = idGen.buildURIFromIdentifiedBy(identifed, baseURI);
        assertNotNull(uri);
    }

    /**
     * Test of getIdentiferForURI method, of class IdentifierGenerator.
     */
    @Test
    public void testGetIdentiferForURI() {
        Identified obj = new Identified("bleh","newudata");
        Identified created = repo.create(obj);
        
        try{
            URIImpl uri = new URIImpl(created.getIdentifier().getUri().toString());
            Identifier identifier = idGen.getIdentiferForURI(uri);
            assertNotNull(identifier);
            assertEquals(identifier.getUri(),created.getIdentifier().getUri());
            assertEquals(identifier.getIdentifier(),created.getIdentifier().getIdentifier());
        }catch(StorageException ex){
            fail();
        }
    }

    /**
     * Test of buildIdentifier method, of class IdentifierGenerator.
     */
    @Test
    public void testBuildIdentifier() {
        Identified obj = new Identified("bleh","newudata");
        UUID uuid = UUID.randomUUID();
        URIImpl uri = new URIImpl("http://nowhere/"+uuid.toString());
        
        
        Identifier id = idGen.buildIdentifier(obj, uri, uuid.toString());
        assertNotNull(id);
        assertEquals(id.getIdentifier(),uuid.toString());
        assertEquals(id.getUri().toString(),uri.toString());

    }

    /**
     * Test of buildURIFromIdentifier method, of class IdentifierGenerator.
     */
    @Test
    public void testBuildURIFromIdentifier() {
        Identifier id = new Identifier();
        String uuid = UUID.randomUUID().toString();
        id.setIdentifier(uuid);
        String uriBase = "http://nowhere/";
        
        URI uri = idGen.buildURIFromIdentifier(id, uriBase);
        assertNotNull(uri);
        assertEquals(uri.toString(),uriBase + uuid);
        
    }
}