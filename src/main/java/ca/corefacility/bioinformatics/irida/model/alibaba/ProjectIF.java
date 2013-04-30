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
package ca.corefacility.bioinformatics.irida.model.alibaba;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.openrdf.annotations.Iri;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Iri(ProjectIF.PREFIX + ProjectIF.TYPE)
public interface ProjectIF extends Thing<Audit,Identifier>{
    
    public static final String PREFIX = "http://corefacility.ca/irida/";
    public static final String TYPE = "Project";
    
    @Iri(PREFIX + "projectName")
    public String getName();
    

}
