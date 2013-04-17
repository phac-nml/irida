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
package ca.corefacility.bioinformatics.irida.web.assembler.resource.project;

import ca.corefacility.bioinformatics.irida.model.Project;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.ResourceSupport;

/**
 * Wraps a collection of {@link ProjectResource} to send back to the user.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "projects")
public class ProjectCollectionResource extends ResourceSupport {

    @XmlElement(name = "project")
    private List<ProjectResource> projects;

    public ProjectCollectionResource() {
        this.projects = new ArrayList<>();
    }

    public void add(ProjectResource project) {
        this.projects.add(project);
    }
}
