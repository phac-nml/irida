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
package ca.corefacility.bioinformatics.irida.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class PropertyMapper {
    public List<Property> props;
    public String prefix;
    public String type;
        
    public PropertyMapper(String prefix, String type){
        props = new ArrayList<>();
        this.prefix = prefix;
        this.type = type;
    }
    
    public void addProperty(String prefix, String predicate, String variable, Method getter, Method setter, Class type){
        Property p = new Property(prefix, predicate, variable, getter, setter,  type);
        props.add(p);
    }
    
    public List<Property> getProperties(){
        return props;
    }
    
    public class Property{
        public String prefix;
        public String predicate;
        public String variable;
        public Method getter;
        public Method setter;
        public Class type;
        
        public Property(String prefix, String predicate, String variable, Method getter, Method setter, Class type){
            this.prefix = prefix;
            this.predicate = predicate;
            this.variable = variable;
            this.getter = getter;
            this.setter = setter;
            this.type = type;
        }
    }

    //"username" "getUsername" "setUsername" "String"
}
