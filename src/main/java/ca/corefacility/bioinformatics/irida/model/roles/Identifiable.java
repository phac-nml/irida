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
package ca.corefacility.bioinformatics.irida.model.roles;

/**
 * An entity object may implement the {@link Identifiable} interface if it
 * intends to be persisted to the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface Identifiable<IdentifierType> {

    /**
     * Get the identifier for this entity.
     *
     * @return the identifier for this entity.
     */
    public IdentifierType getIdentifier();

    /**
     * Set a new identifier for this entity.
     *
     * @param identifier the new identifier.
     */
    public void setIdentifier(IdentifierType identifier);
}
