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
package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Map;

/**
 * Map of fields with an associated identifier
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class FieldMap implements Identifiable<Identifier> {
    private Identifier identifier;
    private Map<String,Object> fields;

    public FieldMap(){}
    
    public FieldMap(Identifier identifier, Map<String, Object> fields) {
        this.identifier = identifier;
        this.fields = fields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the Map of fields
     * @return A Map<String,Object> of the retrieved fields
     */
    public Map<String, Object> getFields() {
        return fields;
    }

    /**
     * Set the fields for this object
     * @param fields A Map<String,Object> of fields to store
     */
    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
    
    /**
     * Get the mapped field for the given key
     * @param key The key to retrieve
     * @return The Object value for the given key
     */
    public Object get(String key){
        return fields.get(key);
    }
    
}
