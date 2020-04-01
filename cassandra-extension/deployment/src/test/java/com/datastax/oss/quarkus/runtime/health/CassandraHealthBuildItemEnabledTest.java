package com.datastax.oss.quarkus.runtime.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.quarkus.CassandraTestBase;
import io.quarkus.arc.Arc;
import io.quarkus.test.QuarkusUnitTest;
import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class CassandraHealthBuildItemEnabledTest {
  @RegisterExtension
  static QuarkusUnitTest runner =
      new QuarkusUnitTest()
          .setArchiveProducer(
              () -> ShrinkWrap.create(JavaArchive.class).addClasses(CassandraTestBase.class))
          .withConfigurationResource("application-health-enabled.properties");

  @Test
  public void shouldHaveHealthCheckInTheContainer() {
    Set<Bean<?>> beans = Arc.container().beanManager().getBeans(CassandraHealthCheck.class);
    assertThat(beans.size()).isEqualTo(1);
  }
}
