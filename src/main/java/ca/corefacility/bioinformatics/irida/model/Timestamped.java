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

import java.util.Date;

/**
 * An interface for elements which need to have a timestamp associated with them.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface Timestamped {
    /**
     * Get the timestamp for this object
     * @return A {@link Date} object of the timestamp
     */
    public Date getTimestamp();
    
    /**
     * Set the timestamp for this object
     * @param timestamp a {@link Date} timestamp to set for this object
     */
    public void setTimestamp(Date timestamp);
    
}
