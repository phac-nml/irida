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

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.openrdf.query.BindingSet;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserResultExtractor {
    
    /**
     * Extract the data from a BiningSet to build a User object
     * @param id The ID object of the user
     * @param bindingSet The binding set to build by
     * @return A newly constructed User
     */
    public static User extractData(Identifier id, BindingSet bindingSet){
        User usr = new User();
        usr.setIdentifier(id);
        
        usr = buildUserProperties(bindingSet, usr);
        
        return usr;
    }
    
    private static User buildUserProperties(BindingSet bs, User usr){      
        usr.setUsername(bs.getValue("username").stringValue());
        usr.setEmail(bs.getValue("email").stringValue());
        usr.setFirstName(bs.getValue("firstName").stringValue());
        usr.setLastName(bs.getValue("lastName").stringValue());
        usr.setPhoneNumber(bs.getValue("phoneNumber").stringValue());
        
        return usr;
    }
}
