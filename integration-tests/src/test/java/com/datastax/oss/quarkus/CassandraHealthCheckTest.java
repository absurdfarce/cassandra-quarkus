/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.quarkus;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
public class CassandraHealthCheckTest {
    @Test
    public void healthCheckShouldReportStatusUp() {
        // when
        Response response = RestAssured.with().get("/health/ready");

        // then
        Assertions.assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.statusCode());

        Map<String, Object> body = response.as(new TypeRef<Map<String, Object>>() {
        });
        Assertions.assertEquals("UP", body.get("status"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> checks = (List<Map<String, Object>>) body.get("checks");
        Assertions.assertEquals(1, checks.size());
        Assertions.assertEquals("DataStax Apache Cassandra Driver health check", checks.get(0).get("name"));
    }
}
