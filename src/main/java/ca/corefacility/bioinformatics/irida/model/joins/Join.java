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
package ca.corefacility.bioinformatics.irida.model.joins;

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import java.util.Date;

/**
 * Interface that the join classes should extend.  Classes that extend this can add additional fields that can be persisted in the database.
 * Implementations will have a "subject" and "object" that are the 2 fields being joined, then a creation timestamp.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface Join<SubjectType extends IridaThing, ObjectType extends IridaThing> {
    public SubjectType getSubject();
    public void setSubject(SubjectType subject);
    
    public ObjectType getObject();
    public void setObject(ObjectType object);
    
    public Date getTimestamp();
    public void setTimestamp(Date timestamp);
}
