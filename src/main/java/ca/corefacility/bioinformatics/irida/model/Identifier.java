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
package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

/**
 * Any object that wishes to be uniquely identifiable in the database should
 * have an {@link Identifier} data member. An example of composition over
 * inheritance, see http://en.wikipedia.org/wiki/Liskov_substitution_principle
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class Identifier {

    @NotNull
    private UUID uuid;
    @NotNull
    private URI uri;
    // alternative URI:
    //@URL
    //private String url;

    public Identifier() {
        uuid = UUID.randomUUID();
    }

    public Identifier(URI uri, UUID uuid) {
        this.uri = uri;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Identifier) {
            Identifier id = (Identifier) other;
            return Objects.equals(this.uuid, id.uuid)
                    && Objects.equals(uri, id.uri);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, uuid);
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}
