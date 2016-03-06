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

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.domain.docker.Promote;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Mock tests for the {@link DockerApi}
 * class.
 */
@Test(groups = "unit", testName = "DockerApiMockTest")
public class DockerApiMockTest extends BaseArtifactoryMockTest {

    public void testPromote() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      String payload = payloadFromResource("/promote.json");
      server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      DockerApi api = jcloudsApi.dockerApi();
      try {
          Promote dockerPromote = Promote.create("docker-promoted", "library/artifactory", "latest", false);
          boolean success = api.promote("docker-local", dockerPromote);
          assertTrue(success);
          assertSent(server, "POST", "/api/docker/docker-local/v1/promote", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
    }

    public void testPromoteNonExistentImage() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = "Unable to find tag file at 'repositories/library/artifactory/latest/tag.json'";
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        DockerApi api = jcloudsApi.dockerApi();
        try {
            Promote dockerPromote = Promote.create("docker-promoted", "library/artifactory", "latest", false);
            boolean success = api.promote("docker-local", dockerPromote);
            assertFalse(success);
            assertSent(server, "POST", "/api/docker/docker-local/v1/promote", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }
}
