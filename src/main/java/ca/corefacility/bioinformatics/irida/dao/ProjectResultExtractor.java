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
package ca.corefacility.bioinformatics.irida.dao;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.openrdf.query.BindingSet;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectResultExtractor {
    public static Project extractData(Identifier id, BindingSet bindingSet){
        Project project = new Project();
        project.setIdentifier(id);
        
        project = buildProjectProperties(bindingSet, project);
        
        return project;
    }
    
    public static Project buildProjectProperties(BindingSet bs, Project proj){      
        proj.setName(bs.getValue("name").stringValue());
        return proj;
    }   
}
