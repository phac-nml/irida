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
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    public RelationshipSesameRepositoryTest() {
    }

    @Before
    public void setUp() throws IOException {
        File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        logger.debug("Database stored in [" + tempDir + "]");
        SailStore store = new SailStore(tempDir);
        store.initialize();
        AuditRepository auditRepo = new AuditRepository(store);
        linksRepo = new RelationshipSesameRepository(store, auditRepo);
        repo = new IdentifiedRepo(store, auditRepo, linksRepo);


        pred = new RdfPredicate("irida", "identifiedRelationship");
        linksRepo.addRelationship(Identified.class, pred, Identified.class);

        Identified i1 = repo.create(new Identified("first"));
        Identified i2 = repo.create(new Identified("second"));

        first = i1.getIdentifier();
        second = i2.getIdentifier();

        linksRepo.create(i1, i2);
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