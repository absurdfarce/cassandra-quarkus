package io.quarkus.cassandra.runtime.health;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

import io.quarkus.arc.Arc;

@Readiness
@ApplicationScoped
public class CassandraHealthCheck implements HealthCheck {
    /** Name of the health-check. */
    private static final String HEALTH_CHECK_NAME = "DataStax Apache Cassandra Driver health check";

    static final String HEALTH_CHECK_QUERY = "SELECT data_center, release_version, cluster_name, cql_version FROM system.local";

    private CqlSession cqlSession;

    public CqlSession beanProvider() {
        return Arc.container().instance(CqlSession.class).get();
    }

    @PostConstruct
    protected void init() {
        this.cqlSession = beanProvider();
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(HEALTH_CHECK_NAME).up();

        try {
            Row result = cqlSession.execute(HEALTH_CHECK_QUERY).one();
            if (result == null) {
                return builder.down().withData("reason", "system.local returned null").build();
            }

            for (Map.Entry<String, String> entry : extractInfoFromResult(result).entrySet()) {
                builder.withData(entry.getKey(), entry.getValue());
            }
            return builder
                    .withData("numberOfNodes", cqlSession.getMetadata().getNodes().size())
                    .up()
                    .build();
        } catch (Exception ex) {
            return builder.down().withData("reason", ex.getMessage()).build();
        }
    }

    private Map<String, String> extractInfoFromResult(Row result) {
        HashMap<String, String> details = new HashMap<>();
        details.put("dataCenter", result.getString("data_center"));
        details.put("releaseVersion", result.getString("release_version"));
        details.put("clusterName", result.getString("cluster_name"));
        details.put("cqlVersion", result.getString("cql_version"));
        return details;
    }
}
