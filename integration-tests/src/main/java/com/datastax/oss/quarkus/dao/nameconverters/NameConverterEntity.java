package com.datastax.oss.quarkus.dao.nameconverters;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.NamingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
@NamingStrategy(customConverterClass = TestNameConverter.class)
public class NameConverterEntity {

    @PartitionKey
    private int entityId;

    public NameConverterEntity() {
    }

    public NameConverterEntity(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}