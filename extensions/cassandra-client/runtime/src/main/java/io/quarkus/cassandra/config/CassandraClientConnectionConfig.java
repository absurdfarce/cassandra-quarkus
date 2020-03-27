package io.quarkus.cassandra.config;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.datastax.oss.driver.api.core.CqlSession;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class CassandraClientConnectionConfig {
    /**
     * Contact-points used to connect to Cassandra.
     * If not specified, it will connect to local-host.
     */
    @ConfigItem(name = "contact-points", defaultValue = "127.0.0.1:9042")
    public List<String> contactPoints;

    /**
     * Local data-center used when creating a {@link CqlSession}
     */
    @ConfigItem(name = "load-balancing-policy.local-datacenter")
    public String localDataCenter;

    /**
     * How long the driver waits for a request to complete.
     */
    @ConfigItem(name = "request.timeout")
    public Optional<Duration> requestTimeout;

}
