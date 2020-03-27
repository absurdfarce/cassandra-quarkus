package io.quarkus.cassandra.runtime.health;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.arc.Arc;
import io.quarkus.cassandra.CassandraTestBase;
import io.quarkus.test.QuarkusUnitTest;

public class CassandraHealthBuildItemEnabledTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClasses(CassandraTestBase.class))
            .withConfigurationResource("application-health-enabled.properties");

    @Test
    public void shouldHaveHealthCheckInTheContainer() {
        Set<Bean<?>> beans = Arc.container().beanManager().getBeans(CassandraHealthCheck.class);
        assertThat(beans.size()).isEqualTo(1);
    }
}
