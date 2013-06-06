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
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import org.openrdf.model.URI;

/**
 * Class for generating identifiers for objects of type User.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserIdentifierGenerator extends IdentifierGenerator<User>{

    public UserIdentifierGenerator(TripleStore store) {
        super(store);
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Identifier generateNewIdentifier(User obj, String baseURI) {
        if(obj == null){
            throw new IllegalArgumentException("User cannot be null when creating new identifiers");
        }
        
        java.net.URI objuri = buildURIFromIdentifiedBy(obj.getUsername(),baseURI);
        UserIdentifier ui = new UserIdentifier(obj.getUsername());
        ui.setUri(objuri);
        ui.setLabel(obj.getLabel());
        return ui;
    }
    
    /**
     * {@inheritDoc}
     */  
    @Override
    public Identifier buildIdentifier(User object, URI uri, String identifiedBy) {
        UserIdentifier objid = new UserIdentifier();
        
        objid.setUri(java.net.URI.create(uri.toString()));
        objid.setIdentifier(object.getUsername());
        objid.setLabel(object.getLabel());
        
        return objid;
    }
    
}
