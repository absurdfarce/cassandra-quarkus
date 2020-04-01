package com.datastax.oss.quarkus.dao.nameconverters;

import com.datastax.oss.driver.api.mapper.entity.naming.NameConverter;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TestNameConverter implements NameConverter {

    @Override
    @NonNull
    public String toCassandraName(@NonNull String javaName) {
        return "test_" + javaName;
    }
}
