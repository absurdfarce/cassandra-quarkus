/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.quarkus.runtime;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfig;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import com.datastax.oss.quarkus.config.CassandraClientConfig;
import com.datastax.oss.quarkus.config.CassandraClientConnectionConfig;
import com.typesafe.config.ConfigFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.concurrent.CompletionStage;

public abstract class AbstractCassandraClientProducer {

  private CassandraClientConfig config;

  public void setCassandraClientConfig(CassandraClientConfig config) {
    this.config = config;
  }

  private ProgrammaticDriverConfigLoaderBuilder createDriverConfigLoader() {
    return new DefaultProgrammaticDriverConfigLoaderBuilder(
        () -> {
          ConfigFactory.invalidateCaches();
          return ConfigFactory.defaultOverrides()
              // todo do we need to load both conf and json?
              .withFallback(ConfigFactory.parseResources("application.conf"))
              .withFallback(ConfigFactory.parseResources("application.json"))
              .withFallback(ConfigFactory.defaultReference())
              .resolve();
        },
        DefaultDriverConfigLoader.DEFAULT_ROOT_PATH) {
      @NonNull
      @Override
      public DriverConfigLoader build() {
        return new NonReloadableDriverConfigLoader(super.build());
      }
    };
  }

  CassandraClientConfig getCassandraClientConfig() {
    return config;
  }

  public CqlSession createCassandraClient(CassandraClientConfig config) {
    ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder = createDriverConfigLoader();
    configureConnectionSettings(configLoaderBuilder, config.cassandraClientConnectionConfig);
    CqlSessionBuilder builder = CqlSession.builder().withConfigLoader(configLoaderBuilder.build());
    return builder.build();
  }

  private void configureConnectionSettings(
      ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder,
      CassandraClientConnectionConfig config) {
    configLoaderBuilder.withStringList(DefaultDriverOption.CONTACT_POINTS, config.contactPoints);
    configLoaderBuilder.withString(
        DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, config.localDataCenter);
    config.requestTimeout.ifPresent(
        v -> configLoaderBuilder.withDuration(DefaultDriverOption.REQUEST_TIMEOUT, v));
    // force java clock because jnr does not work with quarkus
    configLoaderBuilder.withBoolean(DefaultDriverOption.TIMESTAMP_GENERATOR_FORCE_JAVA_CLOCK, true);
  }

  private static class NonReloadableDriverConfigLoader implements DriverConfigLoader {

    private final DriverConfigLoader delegate;

    public NonReloadableDriverConfigLoader(DriverConfigLoader delegate) {
      this.delegate = delegate;
    }

    @NonNull
    @Override
    public DriverConfig getInitialConfig() {
      return delegate.getInitialConfig();
    }

    @Override
    public void onDriverInit(@NonNull DriverContext context) {
      delegate.onDriverInit(context);
    }

    @NonNull
    @Override
    public CompletionStage<Boolean> reload() {
      return CompletableFutures.failedFuture(
          new UnsupportedOperationException("reload not supported"));
    }

    @Override
    public boolean supportsReloading() {
      return false;
    }

    @Override
    public void close() {
      delegate.close();
    }
  }
}
