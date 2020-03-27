package com.datastax.oss.quarkus.dao;

import java.util.Objects;
import java.util.UUID;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
public class Product {
    @PartitionKey
    private UUID id;
    private String description;

    public Product() {
    }

    public Product(UUID id, String description) {
        this.id = id;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product that = (Product) o;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "ProductSimple{" + "id=" + id + ", description='" + description + '\'' + '}';
    }
}