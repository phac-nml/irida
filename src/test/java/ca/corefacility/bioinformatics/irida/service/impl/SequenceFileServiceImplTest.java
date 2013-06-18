/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SequenceFileServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplTest.class);
    private SequenceFileService sequenceFileService;
    private SequenceFileRepository crudRepository;
    private CRUDRepository<Identifier, SequenceFile> fileRepository;
    private RelationshipRepository relationshipRepository;
    private Validator validator;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = mock(SequenceFileRepository.class);
        fileRepository = mock(CRUDRepository.class);
        relationshipRepository = mock(RelationshipRepository.class);
        sequenceFileService = new SequenceFileServiceImpl(crudRepository, fileRepository, relationshipRepository, validator);
    }

    @Test
    public void testCreateFile() throws IOException, NoSuchFieldException {
        Path f = Files.createTempFile(null, null);

        SequenceFile sf = new SequenceFile(f);
        SequenceFile withIdentifier = new SequenceFile(new Identifier(), f);
        when(crudRepository.create(sf)).thenReturn(withIdentifier);
        when(fileRepository.create(withIdentifier)).thenReturn(withIdentifier);
        when(crudRepository.update(withIdentifier.getIdentifier(),
                ImmutableMap.of("file", (Object) withIdentifier.getFile()))).thenReturn(withIdentifier);

        when(crudRepository.exists(withIdentifier.getIdentifier())).thenReturn(Boolean.TRUE);

        SequenceFile created = sequenceFileService.create(sf);

        assertEquals(created, withIdentifier);

        verify(crudRepository).create(sf);
        verify(fileRepository).create(withIdentifier);
        verify(crudRepository).update(withIdentifier.getIdentifier(),
                ImmutableMap.of("file", (Object) withIdentifier.getFile()));
        verify(crudRepository).exists(withIdentifier.getIdentifier());
        Files.delete(f);
    }

    @Test
    public void testUpdateWithoutFile() throws IOException, NoSuchFieldException {
        Identifier updatedId = new Identifier();
        Identifier originalId = new Identifier();
        Path f = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile(originalId, f);
        SequenceFile updatedSf = new SequenceFile(updatedId, f);

        ImmutableMap<String, Object> updatedMap = ImmutableMap.of("identifier", (Object) updatedId);

        when(crudRepository.exists(originalId)).thenReturn(Boolean.TRUE);
        when(crudRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);

        sf = sequenceFileService.update(originalId, updatedMap);

        assertEquals(updatedId, sf.getIdentifier());

        verify(crudRepository).exists(originalId);
        verify(crudRepository).update(originalId, updatedMap);
        verify(fileRepository, times(0)).update(sf.getIdentifier(), updatedMap);
        Files.delete(f);
    }

    @Test
    public void testUpdateWithFile() throws IOException, NoSuchFieldException {
        Identifier id = new Identifier();
        Path originalFile = Files.createTempFile(null, null);
        Path updatedFile = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile(id, originalFile);
        SequenceFile updatedSf = new SequenceFile(id, updatedFile);

        ImmutableMap<String, Object> updatedMap = ImmutableMap.of("file", (Object) updatedFile);

        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);
        when(fileRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);

        sf = sequenceFileService.update(id, updatedMap);

        assertEquals(updatedFile, sf.getFile());

        // each is called twice, once to update other possible fields, once
        // again after an updated file has been dropped into the appropriate
        // directory
        verify(crudRepository, times(2)).exists(id);
        verify(crudRepository, times(2)).update(sf.getIdentifier(), updatedMap);
        verify(fileRepository).update(sf.getIdentifier(), updatedMap);

        Files.delete(originalFile);
        Files.delete(updatedFile);
    }

    @Test
    public void testCreateSequenceFileWithOwner() throws IOException {
        Path file = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile(file);
        sf.setIdentifier(new Identifier());
        Identifier owner = new Identifier();
        Relationship r = new Relationship(owner, sf.getIdentifier());

        when(crudRepository.create(sf)).thenReturn(sf);
        when(crudRepository.update(any(Identifier.class), any(Map.class))).thenReturn(sf);
        when(crudRepository.exists(sf.getIdentifier())).thenReturn(true);
        when(fileRepository.create(sf)).thenReturn(sf);
        when(relationshipRepository.create(Project.class, owner, SequenceFile.class, sf.getIdentifier())).thenReturn(r);

        Relationship created = sequenceFileService.createSequenceFileWithOwner(sf, Project.class, owner);

        verify(crudRepository).create(sf);
        verify(crudRepository).update(any(Identifier.class), any(Map.class));
        verify(crudRepository).exists(sf.getIdentifier());
        verify(fileRepository).create(sf);
        verify(relationshipRepository).create(Project.class, owner, SequenceFile.class, sf.getIdentifier());

        assertNotNull(created);
        assertEquals(owner, created.getSubject());
        assertEquals(sf.getIdentifier(), created.getObject());

        Files.delete(file);
    }

    @Test
    public void testGetSequenceFileFromProject() throws IOException {
        Path file = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile(file);
        sf.setIdentifier(new Identifier());
        Project p = new Project();
        p.setIdentifier(new Identifier());
        Relationship r = new Relationship(p.getIdentifier(), sf.getIdentifier());

        when(crudRepository.read(sf.getIdentifier())).thenReturn(sf);
        when(crudRepository.exists(sf.getIdentifier())).thenReturn(true);
        when(relationshipRepository.getLinks(p.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier()))
                .thenReturn(Lists.newArrayList(r));

        SequenceFile read = sequenceFileService.getSequenceFileFromProject(p, sf.getIdentifier());

        verify(crudRepository).exists(sf.getIdentifier());
        verify(crudRepository).read(sf.getIdentifier());
        verify(relationshipRepository).getLinks(p.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier());

        assertNotNull(read);
        assertEquals(read, sf);

        Files.delete(file);
    }

    @Test
    public void testGetSequenceFileFromSample() throws IOException {
        Path file = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile(file);
        sf.setIdentifier(new Identifier());
        Project p = new Project();
        p.setIdentifier(new Identifier());
        Sample s = new Sample();
        s.setIdentifier(new Identifier());
        Relationship projectSample = new Relationship(p.getIdentifier(), s.getIdentifier());
        Relationship sampleSequenceFile = new Relationship(s.getIdentifier(), sf.getIdentifier());

        when(crudRepository.read(sf.getIdentifier())).thenReturn(sf);
        when(crudRepository.exists(sf.getIdentifier())).thenReturn(true);
        when(relationshipRepository.getLinks(p.getIdentifier(), RdfPredicate.ANY, s.getIdentifier()))
                .thenReturn(Lists.newArrayList(projectSample));
        when(relationshipRepository.getLinks(s.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier()))
                .thenReturn(Lists.newArrayList(sampleSequenceFile));

        SequenceFile read = sequenceFileService.getSequenceFileFromSample(p, s, sf.getIdentifier());

        verify(crudRepository).exists(sf.getIdentifier());
        verify(crudRepository).read(sf.getIdentifier());
        verify(relationshipRepository).getLinks(p.getIdentifier(), RdfPredicate.ANY, s.getIdentifier());
        verify(relationshipRepository).getLinks(s.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier());

        assertNotNull(read);
        assertEquals(read, sf);

        Files.delete(file);
    }
}
