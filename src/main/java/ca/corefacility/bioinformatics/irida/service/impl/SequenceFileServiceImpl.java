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

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validator;

/**
 * Implementation for managing {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImpl extends CRUDServiceImpl<Identifier, SequenceFile> {

    private CRUDRepository<Identifier, SequenceFile> fileRepository;

    /**
     * Constructor.
     *
     * @param sequenceFileRepository the sequence file repository.
     * @param validator validator.
     */
    public SequenceFileServiceImpl(
            SequenceFileRepository sequenceFileRepository,
            CRUDRepository<Identifier, SequenceFile> fileRepository,
            Validator validator) {
        super(sequenceFileRepository, validator, SequenceFile.class);
        this.fileRepository = fileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceFile create(SequenceFile sequenceFile) {
        // Send the file to the database repository to be stored (in super)
        sequenceFile = super.create(sequenceFile);
        // Then store the file in an appropriate directory
        sequenceFile = fileRepository.create(sequenceFile);
        // And finally, update the database with the stored file location

        sequenceFile = super.update(sequenceFile.getIdentifier(),
                ImmutableMap.of("file", (Object) sequenceFile.getFile()));
        return sequenceFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceFile update(Identifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        SequenceFile updated = super.update(id, updatedFields);

        if (updatedFields.containsKey("file")) {
            try {
                updated = fileRepository.update(id,updatedFields);
                updated = super.update(id, ImmutableMap.of("file",
                        (Object) updated.getFile()));
            } catch (NoSuchFieldException ex) {
                throw new InvalidPropertyException("A property of this object could not be updated.");
            }
        }

        return updated;
    }
    
    public void addFileToProject(Project project, SequenceFile file){
        sequenceFileRepository().addFileToProject(project, file);
    }
    
    public void addFileToSample(Sample sample, SequenceFile file){
        sequenceFileRepository().addFileToSample(sample, file);
    }
    
    public List<SequenceFile> getFilesForSample(Sample sample){
        return sequenceFileRepository().getFilesForSample(sample);
    }
    
    public List<SequenceFile> getFilesForProject(Project project){
        return sequenceFileRepository().getFilesForProject(project);
    }   
    
    public SequenceFileRepository sequenceFileRepository(){
        return (SequenceFileRepository) repository;
    }
    
}
