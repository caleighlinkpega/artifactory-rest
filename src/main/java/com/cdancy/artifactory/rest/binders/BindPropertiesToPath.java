/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdancy.artifactory.rest.binders;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import javax.inject.Singleton;
import java.util.IllegalFormatException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class BindPropertiesToPath implements Binder {
    @Override
    public <R extends HttpRequest> R bindToRequest(final R request, final Object properties) {

        checkArgument(properties instanceof Map, "binder is only valid for Map");
        Map<String, String> props = (Map<String, String>) properties;
        checkArgument(props.size() > 0, "properties Map cannot be empty");

        StringBuilder propertiesProp = new StringBuilder();
        for (Map.Entry<String, String> prop : props.entrySet()) {
            String potentialKey = prop.getKey().trim();
            if (potentialKey.length() > 0) {
                propertiesProp.append(potentialKey);
                if (prop.getValue() != null) {
                    String potentialValue = prop.getValue().trim();
                    if (potentialValue.length() > 0)
                        propertiesProp.append("=").append(potentialValue);
                }
                propertiesProp.append(",");
            }
        }


        if (propertiesProp.length() == 0) {
            throw new IllegalArgumentException("properties did not have any valid key/value pairs");
        }

        if (propertiesProp.charAt(propertiesProp.length() - 1) == ',') {
            propertiesProp.setLength(propertiesProp.length() - 1);
        }

        return (R) request.toBuilder().addQueryParam("properties", propertiesProp.toString()).build();
    }
}