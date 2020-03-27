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
