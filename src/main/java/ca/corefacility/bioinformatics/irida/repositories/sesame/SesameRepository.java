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

import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SesameRepository {
    
    TripleStore store;
    String URI;
    
    public SesameRepository(){};
    
    public SesameRepository(TripleStore store,String type){
        this.store = store;
        URI = store.getURI() + type + "/";
    }     
    
    public static String getParameters(String subject,HashMap<String,String> pmap){
        subject = "?" + subject;
        
        String params = "";
        
        Set<String> keys = pmap.keySet();
        for(String key : keys){
            String val = pmap.get(key);
            params += subject + " " + key + " " + val + ". \n";
        }
        
        return params;
    }
    
    public void close() {
        store.close();
    }    
}
