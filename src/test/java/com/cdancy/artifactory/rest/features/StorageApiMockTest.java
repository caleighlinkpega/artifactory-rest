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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.storage.ItemProperties;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock tests for the {@link com.cdancy.artifactory.rest.features.StorageApi}
 * class.
 */
@Test(groups = "unit", testName = "StorageApiMockTest")
public class StorageApiMockTest extends BaseArtifactoryMockTest {

   public void testSetItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         Map<String, String> props = new HashMap<String, String>();
         props.put("hello", "world");
         boolean itemSet = api.setItemProperties("libs-snapshot-local", "hello/world", props);
         assertTrue(itemSet);
         assertSent(server, "PUT", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=hello%3Dworld");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGetItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/item-properties.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         ItemProperties itemProperties = api.getItemProperties("libs-snapshot-local", "hello/world");
         assertNotNull(itemProperties);
         assertTrue(itemProperties.properties().size() > 0);
         assertTrue(itemProperties.properties().containsKey("hello"));
         assertTrue(itemProperties.properties().get("hello").contains("world"));
         assertSent(server, "GET", "/api/storage/libs-snapshot-local/hello/world");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(204));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         Map<String, String> props = new HashMap<String, String>();
         props.put("hello", "world");
         boolean propertyDeleted = api.deleteItemProperties("libs-snapshot-local", "hello/world", props);
         assertTrue(propertyDeleted);
         assertSent(server, "DELETE", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=hello%3Dworld");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNonExistentItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         Map<String, String> props = new HashMap<String, String>();
         props.put("hello", "world");
         boolean propertyDeleted = api.deleteItemProperties("libs-snapshot-local", "hello/world", props);
         assertFalse(propertyDeleted);
         assertSent(server, "DELETE", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=hello%3Dworld");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}