package com.datastax.oss.quarkus;

import java.util.Collections;
import java.util.Map;

import org.jboss.logging.Logger;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.CassandraQueryWaitStrategy;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class CassandraTestResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOGGER = Logger.getLogger(CassandraTestResource.class);
    private static GenericContainer<?> cassandraContainer;

    @Override
    public Map<String, String> start() {
        cassandraContainer = new CassandraContainer<>();
        cassandraContainer.setWaitStrategy(new CassandraQueryWaitStrategy());
        cassandraContainer.start();
        String exposedPort = String.valueOf(cassandraContainer.getMappedPort(CassandraContainer.CQL_PORT));
        LOGGER.infof("Started %s on port %s", cassandraContainer.getDockerImageName(), exposedPort);
        return Collections.singletonMap("quarkus.cassandra.docker_port", exposedPort);
    }

    @Override
    public void stop() {
        if (cassandraContainer != null && cassandraContainer.isRunning()) {
            cassandraContainer.stop();
        }
    }
}
