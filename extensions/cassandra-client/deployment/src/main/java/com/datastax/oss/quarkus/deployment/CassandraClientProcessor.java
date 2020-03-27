package com.datastax.oss.quarkus.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.core.metadata.MetadataManager;
import com.datastax.oss.quarkus.config.CassandraClientConfig;
import com.datastax.oss.quarkus.runtime.AbstractCassandraClientProducer;
import com.datastax.oss.quarkus.runtime.CassandraClientRecorder;
import io.quarkus.arc.Unremovable;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ConfigurationBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

class CassandraClientProcessor {
  public static final String CASSANDRA_CLIENT = "cassandra-client";

  @SuppressWarnings("unchecked")
  @Record(STATIC_INIT)
  @BuildStep
  BeanContainerListenerBuildItem build(
      RecorderContext recorderContext,
      CassandraClientRecorder recorder,
      BuildProducer<FeatureBuildItem> feature,
      BuildProducer<GeneratedBeanBuildItem> generatedBean) {

    feature.produce(new FeatureBuildItem(CASSANDRA_CLIENT));

    String cassandraClientProducerClassName = getCassandraClientProducerClassName();
    createCassandraClientProducerBean(generatedBean, cassandraClientProducerClassName);

    return new BeanContainerListenerBuildItem(
        recorder.addCassandraClient(
            (Class<? extends AbstractCassandraClientProducer>)
                recorderContext.classProxy(cassandraClientProducerClassName)));
  }

  private String getCassandraClientProducerClassName() {
    return AbstractCassandraClientProducer.class.getPackage().getName()
        + "."
        + "CassandraClientProducer";
  }

  private void createCassandraClientProducerBean(
      BuildProducer<GeneratedBeanBuildItem> generatedBean,
      String cassandraClientProducerClassName) {

    ClassOutput classOutput = new GeneratedBeanGizmoAdaptor(generatedBean);

    try (ClassCreator classCreator =
        ClassCreator.builder()
            .classOutput(classOutput)
            .className(cassandraClientProducerClassName)
            .superClass(AbstractCassandraClientProducer.class)
            .build()) {
      classCreator.addAnnotation(ApplicationScoped.class);

      try (MethodCreator defaultCassandraClient =
          classCreator.getMethodCreator("createDefaultCassandraClient", CqlSession.class)) {
        defaultCassandraClient.addAnnotation(ApplicationScoped.class);
        defaultCassandraClient.addAnnotation(Produces.class);
        defaultCassandraClient.addAnnotation(Default.class);

        // make CqlSession as Unremovable bean
        defaultCassandraClient.addAnnotation(Unremovable.class);

        ResultHandle cassandraClientConfig =
            defaultCassandraClient.invokeVirtualMethod(
                MethodDescriptor.ofMethod(
                    AbstractCassandraClientProducer.class,
                    "getCassandraClientConfig",
                    CassandraClientConfig.class),
                defaultCassandraClient.getThis());

        defaultCassandraClient.returnValue(
            defaultCassandraClient.invokeVirtualMethod(
                MethodDescriptor.ofMethod(
                    AbstractCassandraClientProducer.class,
                    "createCassandraClient",
                    CqlSession.class,
                    CassandraClientConfig.class),
                defaultCassandraClient.getThis(),
                cassandraClientConfig));
      }
    }
  }

  @Record(RUNTIME_INIT)
  @BuildStep
  void configureRuntimePropertiesAndBuildClient(
      CassandraClientRecorder recorder,
      CassandraClientConfig cassandraConfig,
      ConfigurationBuildItem config) {
    recorder.configureRuntimeProperties(cassandraConfig);
  }

  @BuildStep
  @Record(value = RUNTIME_INIT, optional = true)
  CassandraClientBuildItem cassandraClient(CassandraClientRecorder recorder) {
    return new CassandraClientBuildItem(recorder.getClient());
  }

  @BuildStep
  HealthBuildItem addHealthCheck(CassandraClientBuildTimeConfig buildTimeConfig) {
    return new HealthBuildItem(
        "com.datastax.oss.quarkus.runtime.health.CassandraHealthCheck",
        buildTimeConfig.healthEnabled,
        "cassandra");
  }

  @BuildStep
  RuntimeInitializedClassBuildItem runtimeMetadataManager() {
    return new RuntimeInitializedClassBuildItem(MetadataManager.class.getCanonicalName());
  }

  // todo make it work and include reference.conf
  @BuildStep
  NativeImageResourceBuildItem referenceConf() {
    return new NativeImageResourceBuildItem("/reference.conf");
  }
}
