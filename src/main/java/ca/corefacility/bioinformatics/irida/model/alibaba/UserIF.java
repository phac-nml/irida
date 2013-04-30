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
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import org.openrdf.annotations.Iri;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Iri(UserIF.PREFIX + UserIF.TYPE)
public interface UserIF extends Thing<Audit,UserIdentifier>{
    public static final String PREFIX = "http://xmlns.com/foaf/0.1/";
    public static final String TYPE = "Person";
    
    @Iri(UserIF.PREFIX + "nick")
    public String getUsername();
    
    @Iri(UserIF.PREFIX + "mbox")
    public String getEmail();
    
    @Iri(UserIF.PREFIX + "password")
    public String getPassword();
    
    @Iri(UserIF.PREFIX + "firstName")
    public String getFirstName();
    
    @Iri(UserIF.PREFIX + "lastName")
    public String getLastName();
    
    @Iri(UserIF.PREFIX + "phone")
    public String getPhoneNumber();
}
