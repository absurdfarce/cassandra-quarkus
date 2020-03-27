package io.quarkus.cassandra.deployment;

import com.datastax.oss.driver.api.core.CqlSession;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.runtime.RuntimeValue;

public final class CassandraClientBuildItem extends SimpleBuildItem {
    private final RuntimeValue<CqlSession> cqlSession;

    public CassandraClientBuildItem(RuntimeValue<CqlSession> cqlSession) {
        this.cqlSession = cqlSession;
    }

    public RuntimeValue<CqlSession> getCqlSession() {
        return cqlSession;
    }
}
