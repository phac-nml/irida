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

package ca.corefacility.bioinformatics.irida.repositories.remote.model;

import ca.corefacility.bioinformatics.irida.model.Project;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tom
 */
public class RemoteProject extends Project implements RemoteResource{
	protected List<Map<String,String>> links;
	
	public String getIdentifier(){
		return this.getId().toString();
	}
	
	public void setIdentifier(String identifier){
		this.setId(Long.parseLong(identifier));
	}

	public List<Map<String, String>> getLinks() {
		return links;
	}

	public void setLinks(List<Map<String, String>> links) {
		this.links = links;
	}
	
	public void setDateCreated(Date dateCreated){
		this.setTimestamp(dateCreated);
	}
	
	public Date getDateCreated(){
		return getTimestamp();
	}
}
