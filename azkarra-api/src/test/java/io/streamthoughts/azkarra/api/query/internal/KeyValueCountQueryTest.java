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
package io.streamthoughts.azkarra.api.query.internal;

import io.streamthoughts.azkarra.api.model.KV;
import io.streamthoughts.azkarra.api.monad.Try;
import io.streamthoughts.azkarra.api.query.LocalStoreAccessor;
import io.streamthoughts.azkarra.api.query.Queried;
import io.streamthoughts.azkarra.api.streams.KafkaStreamsContainer;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyValueCountQueryTest {

    public static final String STORE_NAME = "storeName";

    @Test
    public void shouldReturnApproximateNumEntries() {

        KeyValueCountQuery query = new KeyValueCountQuery(STORE_NAME);
        KafkaStreamsContainer mkContainer = Mockito.mock(KafkaStreamsContainer.class);

        ReadOnlyKeyValueStore store = mock(ReadOnlyKeyValueStore.class);
        when(store.approximateNumEntries()).thenReturn(42L);
        when(mkContainer.getLocalKeyValueStore(matches(STORE_NAME))).thenReturn(new LocalStoreAccessor<>(() -> store));

        Try<List<KV<String, Long>>> result = query.execute(mkContainer, Queried.immediatly());
        Assertions.assertEquals(1, result.get().size());
        Assertions.assertEquals(KV.of("count", 42L), result.get().get(0));
    }
}