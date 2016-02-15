/*
 * Copyright 2016 javier.
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
package org.jutils.jhardware.util;

import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author javier
 */
public class JVMPropertiesUtils {
    public static void main(String [] args) {
        Properties props = System.getProperties();
        for (final Entry<Object, Object> prop : props.entrySet()) {
            System.out.println(prop.getKey() + ":" + prop.getValue());            
        }
        for (final Entry<String, String> env : System.getenv().entrySet()) {
            System.out.println(env.getKey() + ":" + env.getValue());            
        }
        
    }
}
