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
package io.streamthoughts.azkarra.http.security.auth;

import java.security.Principal;
import java.security.cert.X509Certificate;

public class SSLClientAuthentication extends AbstractAuthentication<X509CertificateCredentials> {

    /**
     * Creates a new {@link AbstractAuthentication} instance.
     *
     * @param principal   the {@link Principal} of the user to authenticate.
     * @param credentials the {@link Credentials} of the user to authenticate.
     */
    public SSLClientAuthentication(final Principal principal,
                                   final X509CertificateCredentials credentials) {
        super(principal, credentials);
    }

    public X509Certificate getCertificate() {
        return getCredentials().getX509Certificate();
    }
}
