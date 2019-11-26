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
package io.streamthoughts.azkarra.streams.banner;

import io.streamthoughts.azkarra.api.banner.Banner;
import io.streamthoughts.azkarra.api.banner.BannerPrinter;

import java.io.PrintStream;
import java.util.Objects;

public class DefaultBannerPrinter implements BannerPrinter {

    private PrintStream printStream;

    /**
     * Creates a new {@link DefaultBannerPrinter} instance.
     *
     * @param printStream   the {@link PrintStream} to be used for printing the {@link Banner}.
     */
    DefaultBannerPrinter(final PrintStream printStream) {
        Objects.requireNonNull(printStream, "printStream cannot be null");
        this.printStream = printStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(final Banner banner) {
        banner.print(printStream);
    }
}
