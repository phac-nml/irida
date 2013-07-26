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
package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import java.util.List;

/**
 * A repository for storing Sample objects
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface SampleRepository extends CRUDRepository<Long, Sample>{
    
    /**
     * Get the {@link Sample}s associated with a {@link Project}
     * @param project The {@link Project} to get {@link Sample}s from
     * @return A List of {@link ProjectSampleJoin}s describing the project/sample relationship
     */
    public List<ProjectSampleJoin> getSamplesForProject(Project project);
}
