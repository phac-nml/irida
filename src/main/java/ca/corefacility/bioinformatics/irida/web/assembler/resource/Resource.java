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
package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * A generic container for all resources.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class Resource<Type extends Identifiable & Auditable<Audit>> extends ResourceSupport {

    /**
     * the resource exposed by this container (not serialized).
     */
    @JsonIgnore
    protected Type resource;

    /**
     * Constructor for a resource container.
     *
     * @param resource the resource to be serialized.
     */
    public Resource(Type resource) {
        this.resource = resource;
    }

    /**
     * Set the resource to be serialized.
     *
     * @param resource the resource to be serialized.
     */
    public void setResource(Type resource) {
        this.resource = resource;
    }

    /**
     * All serialized objects should include the creation date of the resource.
     *
     * @return the creation date of the resource.
     */
    @XmlElement
    public Date getDateCreated() {
        return resource.getAuditInformation().getCreated();
    }
}
