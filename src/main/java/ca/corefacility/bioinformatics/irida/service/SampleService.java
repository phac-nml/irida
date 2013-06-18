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
package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

/**
 * A service class for working with samples.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SampleService extends CRUDService<Identifier, Sample> {

    /**
     * Add a {@link SequenceFile} to a {@link Sample}.
     *
     * @param project    the {@Project} to move the {@link SequenceFile} from.
     * @param sample     the {@link Sample} that the {@link SequenceFile} belongs to.
     * @param sampleFile the {@link SequenceFile} that we're adding.
     * @return the {@link Relationship} created between the two entities.
     */
    public Relationship addSequenceFileToSample(Project project, Sample sample, SequenceFile sampleFile);

    /**
     * Get a specific instance of a {@link Sample} that belongs to a {@link Project}. If the {@link Sample} is not
     * associated to the {@link Project} (i.e., no {@link Relationship} is shared between the {@link Sample} and
     * {@link Project}, then an {@link EntityNotFoundException} will be thrown.
     *
     * @param project    the {@link Project} to get the {@link Sample} for.
     * @param identifier the {@link Identifier} of the {@link Sample}
     * @return the {@link Sample} as requested
     * @throws EntityNotFoundException if no {@link Relationship} exists between {@link Sample} and {@link Project}.
     */
    public Sample getSampleForProject(Project project, Identifier identifier) throws EntityNotFoundException;

    /**
     * Move an instance of a {@link SequenceFile} associated with a {@link Sample} to its parent {@link Project}.
     *
     * @param project      the {@link Project} to which we're moving the {@link SequenceFile}.
     * @param sample       the {@link Sample} from which we're moving the {@link SequenceFile}.
     * @param sequenceFile the {@link SequenceFile} that we're moving.
     * @return the new relationship between the {@link Project} and {@link SequenceFile}.
     */
    public Relationship removeSequenceFileFromSample(Project project, Sample sample, SequenceFile sequenceFile);
}
