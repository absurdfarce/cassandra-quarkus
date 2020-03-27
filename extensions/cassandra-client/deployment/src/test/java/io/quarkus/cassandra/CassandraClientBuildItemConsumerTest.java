package io.quarkus.cassandra;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.datastax.oss.driver.api.core.CqlSession;

import io.quarkus.arc.Arc;
import io.quarkus.builder.BuildChainBuilder;
import io.quarkus.cassandra.deployment.CassandraClientBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.test.QuarkusUnitTest;

public class CassandraClientBuildItemConsumerTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class).addClasses(CassandraTestBase.class))
            .withConfigurationResource("application-cassandra-client.properties")
            .addBuildChainCustomizer(buildCustomizer());

    @Test
    public void testContainerHasBeans() {
        // verify that CqlSession bean is present - it must be unremovable to be present at this stage of the lifecycle
        assertThat(Arc.container().instance(CqlSession.class).get()).isNotNull();
    }

    protected static Consumer<BuildChainBuilder> buildCustomizer() {
        return new Consumer<BuildChainBuilder>() {
            // This represents the extension.
            @Override
            public void accept(BuildChainBuilder builder) {
                builder.addBuildStep(context -> {
                    context.consume(CassandraClientBuildItem.class);
                    context.produce(new FeatureBuildItem("dummy"));
                }).consumes(CassandraClientBuildItem.class)
                        .produces(FeatureBuildItem.class)
                        .build();
            }
        };
    }
}
