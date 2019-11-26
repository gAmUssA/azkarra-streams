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
package io.streamthoughts.examples.azkarra.dependency;

import io.streamthoughts.azkarra.api.annotations.Component;
import io.streamthoughts.azkarra.api.components.ComponentModule;

/**
 * A {@link ComponentModule} for providing a new @{link ComplexWordCountTopology} instance.
 */
@Component
public class ComplexWordCountTopologyModule extends ComponentModule<ComplexWordCountTopology> {

    /**
     * Creates a new {@link ComplexWordCountTopologyModule} instance.
     */
    public ComplexWordCountTopologyModule() {
        super(ComplexWordCountTopology.class);
    }

    @Override
    public ComplexWordCountTopology make() {
        StopWordsService service = getComponentForType(StopWordsService.class);
        ComplexWordCountTopology topology = new ComplexWordCountTopology();
        topology.setStopWordsService(service);

        return topology;
    }
}
