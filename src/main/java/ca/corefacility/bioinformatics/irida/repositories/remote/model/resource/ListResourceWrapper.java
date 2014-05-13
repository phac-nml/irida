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


/**
 *
 * @author tom
 * @param <Type>
 */
public class ListResourceWrapper <Type extends RemoteResource> {
	private ResourceList<Type> resource;

	public ResourceList<Type> getResource() {
		return resource;
	}

	public void setResource(ResourceList<Type> resource) {
		this.resource = resource;
	}    
}
