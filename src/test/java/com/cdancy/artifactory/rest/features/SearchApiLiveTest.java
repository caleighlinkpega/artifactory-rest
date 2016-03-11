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
package com.cdancy.artifactory.rest.features;

import com.cdancy.artifactory.rest.domain.search.AQLResult;
import com.cdancy.artifactory.rest.domain.search.BuildArtifact;
import com.cdancy.artifactory.rest.domain.search.SearchBuildArtifacts;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "SearchApiLiveTest")
public class SearchApiLiveTest extends BaseArtifactoryApiLiveTest {

   @Test
   public void testAql() {
      api.storageApi().setItemProperties("jcenter-cache",
              "ant-contrib/ant-contrib/1.0b3/ant-contrib-1.0b3.jar",
              ImmutableMap.of("ant-lib", "true"));
      AQLResult res = api().aql("items.find({\"repo\":{\"$eq\":\"jcenter-cache\"}, \"@ant-lib\":\"true\"})");
      assertNotNull(res);
      assertTrue(res.results().size() > 0);
   }

   @Test
   public void testAqlWithNonAvailableArtifact() {
      AQLResult res = api().aql("items.find({\"repo\":{\"$eq\":\"non-existent-repo\"}, \"@whatever\":\"true\"})");
      assertNotNull(res);
      assertTrue(res.results().size() == 0);
   }

   @Test
   public void testSearchNonExistentBuildArtifacts() {
      SearchBuildArtifacts searchBuildArtifacts = SearchBuildArtifacts.create("ant-contrib-1.0b3.jar",
              "1.0b3",
              null,
              Lists.newArrayList("jcenter-cache"),
              null);
      List<BuildArtifact> res = api().buildArtifacts(searchBuildArtifacts);
      assertNotNull(res);
      assertTrue(res.size() == 0);
   }

   @Test (dependsOnMethods = "testAql")
   public void testPropertySearch() {
      List<String> repos = ImmutableList.of("jcenter-cache");
      Map<String, List<String>> props = ImmutableMap.<String, List<String>>of("ant-lib", ImmutableList.of("true"));
      List<BuildArtifact> res = api().propertySearch(props, repos);
      assertNotNull(res);
      assertTrue(res.size() > 0);
   }

   @Test
   public void testPropertySearchWithNotFoundProperty() {
      List<String> repos = ImmutableList.of("jcenter-cache");
      Map<String, List<String>> props = ImmutableMap.<String, List<String>>of(randomString(), ImmutableList.of("true"));
      List<BuildArtifact> res = api().propertySearch(props, repos);
      assertNotNull(res);
      assertTrue(res.size() == 0);
   }

   private SearchApi api() {
      return api.searchApi();
   }
}
