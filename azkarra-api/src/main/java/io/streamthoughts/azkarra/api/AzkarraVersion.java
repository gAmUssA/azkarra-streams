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
package io.streamthoughts.azkarra.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class AzkarraVersion {

    private static final Logger LOG = LoggerFactory.getLogger(AzkarraVersion.class);

    private static String version = "unknown";
    private static String gitBranch = "unknown";
    private static String gitCommitId = "unknown";

    static {
        try {
            Properties props = new Properties();
            props.load(AzkarraVersion.class.getResourceAsStream("/azkarra-version.properties"));
            version = props.getProperty("version", version).trim();
            gitBranch = props.getProperty("git.branch", gitBranch).trim();
            gitCommitId = props.getProperty("git.commit.id", gitCommitId).trim();
        } catch (Exception e) {
            LOG.warn("Error while loading version: ", e);
        }
    }

    public static String getVersion() {
        return version;
    }

    public static String getGitBranch() {
        return gitBranch;
    }

    public static String getGitCommitId() {
        return gitCommitId;
    }
}
