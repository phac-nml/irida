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

import java.util.List;
import java.util.Map;

/**
 *
 * @author tom
 */
public class ResourceList <Type> {
    
    protected List<Map<String,String>> links;
    List<Type> resources;
    Long totalResources;

    public ResourceList() {
    }

    public List<Type> getResources() {
	return resources;
    }

    public void setResources(List<Type> resources) {
	this.resources = resources;
    }

    public List<Map<String, String>> getLinks() {
	return links;
    }

    public void setLinks(List<Map<String, String>> links) {
	this.links = links;
    }

    public Long getTotalResources() {
	return totalResources;
    }

    public void setTotalResources(Long totalResources) {
	this.totalResources = totalResources;
    }
    
}
