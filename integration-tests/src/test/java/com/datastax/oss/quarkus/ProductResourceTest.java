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

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
public class ProductResourceTest {

    @Test
    public void testSaveAndRetrieveProduct() {
        // create product
        String productId = given()
                .when().post("/cassandra/product/desc1")
                .then()
                .statusCode(200)
                .extract().response().body().asString();
        assertNotNull(productId);

        // retrieve product
        String product = given()
                .when().get("/cassandra/product/" + productId)
                .then()
                .statusCode(200)
                .extract().response().body().toString();
        assertNotNull(product);
    }

    @Test
    public void shouldSaveAndRetrieveUsingCustomNameConverterThatUsesReflection() {
        // create product
        String productId = given()
                .when().post("/cassandra-name-converter/product/100")
                .then()
                .statusCode(200)
                .extract().response().body().asString();
        assertNotNull(productId);

        // retrieve product
        String product = given()
                .when().get("/cassandra-name-converter/product/" + productId)
                .then()
                .statusCode(200)
                .extract().response().body().toString();
        assertNotNull(product);
    }
}
