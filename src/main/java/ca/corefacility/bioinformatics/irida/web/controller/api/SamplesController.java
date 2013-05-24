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
package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller for managing {@link Sample} objects in the system.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@ExposesResourceFor(Sample.class)
@RequestMapping(value = "/samples")
public class SamplesController extends GenericController<Identifier, Sample, SampleResource> {

    /**
     * Constructor, requires a reference to a {@link CRUDService}.
     *
     * @param sampleService the service used to manage samples.
     */
    @Autowired
    public SamplesController(CRUDService<Identifier, Sample> sampleService) {
        super(sampleService, Sample.class, Identifier.class, SampleResource.class);
    }

    /**
     * Map a {@link SampleResource} to an instance of {@link Sample}.
     *
     * @param representation the {@link SampleResource} to map.
     * @return and instance of {@link Sample}.
     */
    @Override
    public Sample mapResourceToType(SampleResource representation) {
        Sample s = new Sample();
        s.setSampleName(representation.getSampleName());
        return s;
    }

}
