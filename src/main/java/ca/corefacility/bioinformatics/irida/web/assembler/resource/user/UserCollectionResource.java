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
package ca.corefacility.bioinformatics.irida.web.assembler.resource.user;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.ResourceSupport;

/**
 * Wraps a collection of {@link UserResource} objects to be sent to the client.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "users")
public class UserCollectionResource extends ResourceSupport {

    @XmlElement(name = "user")
    private List<UserResource> users;

    public UserCollectionResource() {
        this.users = new ArrayList<>();
    }

    /**
     * Add a new {@link UserResource} to this collection.
     *
     * @param u The {@link UserResource} to add.
     */
    public void add(UserResource u) {
        users.add(u);
    }

    /**
     * The collection of {@link UserResource} objects in this collection.
     *
     * @return a collection of {@link UserResource} objects.
     */
    public List<UserResource> getUsers() {
        return this.users;
    }
}
