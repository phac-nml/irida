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

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SampleFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.CRUDMemoryRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import java.io.File;
import java.io.IOException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the business logic for {@link SampleServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImplTest {

    private SampleService sampleService;
    private CRUDRepository<Identifier, Sample> sampleRepository;
    private CRUDRepository<Identifier, SampleFile> sampleFileRepository;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        sampleRepository = new CRUDMemoryRepository<>(Sample.class);
        sampleFileRepository = new CRUDMemoryRepository<>(SampleFile.class);
        this.sampleService = new SampleServiceImpl(sampleRepository, validator);
    }
    
    @Test
    public void testAddSampleFileToSample() throws IOException {
        File temp = File.createTempFile("sampleServiceTestImpl", "test");
        Sample sample = new Sample();
        SampleFile sampleFile = new SampleFile(temp);
        
        sample = sampleRepository.create(sample);
        sampleFile = sampleFileRepository.create(sampleFile);
        
        sampleService.addSampleFileToSample(sample, sampleFile);
        
        sample = sampleRepository.read(sample.getIdentifier());
        sampleFile = sampleFileRepository.read(sampleFile.getIdentifier());
        
        assertTrue(sample.getFiles().contains(sampleFile));
    }
}
