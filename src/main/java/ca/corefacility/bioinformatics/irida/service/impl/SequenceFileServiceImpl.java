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
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import com.google.common.collect.ImmutableMap;

import javax.validation.Validator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation for managing {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImpl extends CRUDServiceImpl<Identifier, SequenceFile> implements SequenceFileService {

    /**
     * A reference to the file system repository.
     */
    private CRUDRepository<Identifier, SequenceFile> fileRepository;
    /**
     * A reference to the data store repository.
     */
    private SequenceFileRepository sequenceFileRepository;
    /**
     * A reference to the {@link RelationshipRepository}.
     */
    private RelationshipRepository relationshipRepository;

    /**
     * Constructor.
     *
     * @param sequenceFileRepository the sequence file repository.
     * @param validator              validator.
     */
    public SequenceFileServiceImpl(
            SequenceFileRepository sequenceFileRepository,
            CRUDRepository<Identifier, SequenceFile> fileRepository,
            RelationshipRepository relationshipRepository,
            Validator validator) {
        super(sequenceFileRepository, validator, SequenceFile.class);
        this.sequenceFileRepository = sequenceFileRepository;
        this.relationshipRepository = relationshipRepository;
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

        Map<String, Object> changed = new HashMap<>();
        changed.put("file", sequenceFile.getFile());
        sequenceFile = super.update(sequenceFile.getIdentifier(), changed);

        return sequenceFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceFile update(Identifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        SequenceFile updated = super.update(id, updatedFields);

        if (updatedFields.containsKey("file")) {
            updated = fileRepository.update(id, updatedFields);
            updated = super.update(id, ImmutableMap.of("file",
                    (Object) updated.getFile()));
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SequenceFile> getSequenceFilesForSample(Sample s) {
        return sequenceFileRepository.getFilesForSample(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship createSequenceFileWithOwner(SequenceFile sequenceFile, Class ownerType, Identifier owner) {
        SequenceFile created = create(sequenceFile);
        Relationship relationship = relationshipRepository.create(ownerType, owner,
                SequenceFile.class, created.getIdentifier());
        return relationship;
    }
}
