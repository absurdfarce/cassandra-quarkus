package com.datastax.oss.quarkus;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.Map;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.wait.CassandraQueryWaitStrategy;

public class CassandraTestBase implements QuarkusTestResourceLifecycleManager {
  private static CassandraContainer<?> cassandraContainer;

  @Override
  public Map<String, String> start() {
    cassandraContainer = new CassandraContainer<>();
    cassandraContainer.setWaitStrategy(new CassandraQueryWaitStrategy());

    // start the container
    cassandraContainer.start();
    return Collections.singletonMap(
        "quarkus.cassandra.docker_port",
        String.valueOf(cassandraContainer.getMappedPort(CassandraContainer.CQL_PORT)));
  }

  @Override
  public void stop() {
    if (cassandraContainer != null && cassandraContainer.isRunning()) {
      cassandraContainer.stop();
    }
  }
}
