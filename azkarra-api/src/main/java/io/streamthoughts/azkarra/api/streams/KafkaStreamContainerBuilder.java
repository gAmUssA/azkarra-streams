/*
 * Copyright 2019 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.azkarra.api.streams;

import io.streamthoughts.azkarra.api.config.Conf;
import io.streamthoughts.azkarra.api.streams.listener.CompositeStateListener;
import io.streamthoughts.azkarra.api.streams.listener.CompositeStateRestoreListener;
import io.streamthoughts.azkarra.api.streams.listener.CompositeUncaughtExceptionHandler;
import io.streamthoughts.azkarra.api.streams.rocksdb.DefaultRocksDBConfigSetter;
import io.streamthoughts.azkarra.api.streams.topology.TopologyContainer;
import io.streamthoughts.azkarra.api.time.Time;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.StateRestoreListener;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default builder class for creating and configuring a new wrapped {@link KafkaStreams} instance.
 */
public class KafkaStreamContainerBuilder {

    private ApplicationId applicationId;
    private TopologyContainer topologyContainer;
    private List<StateRestoreListener> restoreListeners = Collections.emptyList();
    private List<KafkaStreams.StateListener> stateListeners = Collections.emptyList();
    private List<Thread.UncaughtExceptionHandler> exceptionHandlers = Collections.emptyList();

    /**
     * Creates a new {@link KafkaStreamContainerBuilder} instance.
     *
     * @return a new {@link KafkaStreamContainerBuilder} instance.
     */
    public static KafkaStreamContainerBuilder newBuilder() {
        return new KafkaStreamContainerBuilder();
    }

    private KafkaStreamContainerBuilder() {

    }

    public KafkaStreamContainerBuilder withApplicationId(final ApplicationId applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public KafkaStreamContainerBuilder withTopologyContainer(final TopologyContainer topologyContainer) {
        this.topologyContainer = topologyContainer;
        return this;
    }

    public KafkaStreamContainerBuilder withRestoreListeners(final List<StateRestoreListener> listeners) {
        this.restoreListeners = listeners;
        return this;
    }

    public KafkaStreamContainerBuilder withUncaughtExceptionHandler(
            final List<Thread.UncaughtExceptionHandler> handlers) {
        this.exceptionHandlers = handlers;
        return this;
    }

    public KafkaStreamContainerBuilder withStateListeners(final List<KafkaStreams.StateListener> listeners) {
        this.stateListeners = listeners;
        return this;
    }

    /**
     * Builds a {@link KafkaStreams} instance.
     *
     * @return a new {@link KafkaStreamsContainer} instance.
     */
    public KafkaStreamsContainer build() {

        Conf rocksDBConf = Conf.empty();

        // Configure default RocksDB setter class if no one is already defined.
        Conf streamsConfig = topologyContainer.getMetadata().streamsConfig();
        if (!streamsConfig.hasPath(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG)) {
            rocksDBConf = rocksDBConf.withFallback(
                Conf.with(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, DefaultRocksDBConfigSetter.class.getName())
            );
        }

        streamsConfig = Conf
            .with(StreamsConfig.APPLICATION_ID_CONFIG, applicationId.toString())
            .withFallback(streamsConfig)
            .withFallback(rocksDBConf);


        return new KafkaStreamsContainer(
            topologyContainer.getMetadata(),
            streamsConfig,
            new InternalKafkaStreamsFactory(topologyContainer.getTopology())
        );
    }

    private class InternalKafkaStreamsFactory implements KafkaStreamsFactory {

        private final Topology topology;

        private InternalKafkaStreamsFactory(final Topology topology) {
            this.topology = topology;
        }

        @Override
        public KafkaStreams make(final KafkaStreamsContainer container) {
            Objects.requireNonNull(container, "container cannot be null");

            KafkaStreams kafkaStreams = new KafkaStreams(topology, container.streamsConfig().getConfAsProperties());

            final CompositeStateListener compositeStateListener = new CompositeStateListener(stateListeners);
            compositeStateListener.addListener((newState, oldState) -> {
                final long now = Time.SYSTEM.milliseconds();
                container.stateChanges(now, newState, oldState);
            });

            final CompositeUncaughtExceptionHandler handler = new CompositeUncaughtExceptionHandler();
            handler.addHandler((t, e) -> container.setException(e));

            if (exceptionHandlers != null) {
                exceptionHandlers.forEach(handler::addHandler);
            }

            kafkaStreams.setStateListener(compositeStateListener);
            kafkaStreams.setUncaughtExceptionHandler(handler);
            kafkaStreams.setGlobalStateRestoreListener(new CompositeStateRestoreListener(restoreListeners));

            return kafkaStreams;
        }
    }
}
