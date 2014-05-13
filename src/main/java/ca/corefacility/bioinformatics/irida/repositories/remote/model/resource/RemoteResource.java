/*
 * Copyright 2013 tom.
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

package ca.corefacility.bioinformatics.irida.repositories.remote.model.resource;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Methods that must be implemented by resources read from a remote Irida API
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface RemoteResource {
	/**
	 * Get the numeric identifier for this resource
	 * @return
	 */
	public String getIdentifier();
	
	/**
	 * Se the numeric identifier for this resource
	 * @param identifier
	 */
	public void setIdentifier(String identifier);
	
	/**
	 * Get the objects this resource links to
	 * @return
	 */
	public List<Map<String, String>> getLinks();
	
	/**
	 * Set the objects this resource links to
	 * @param links
	 */
	public void setLinks(List<Map<String, String>> links);
	
	/**
	 * Set the date this resource was created
	 * @param dateCreated
	 */
	public void setDateCreated(Date dateCreated);
	
	/**
	 * Get the date this resource was created
	 * @return
	 */
	public Date getDateCreated();
}
