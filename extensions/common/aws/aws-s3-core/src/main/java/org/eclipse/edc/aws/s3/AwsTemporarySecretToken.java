/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.aws.s3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.edc.connector.transfer.spi.types.SecretToken;

public class AwsTemporarySecretToken implements SecretToken {
    private final String sessionToken;
    private final long expiration;
    private final String accessKeyId;
    private final String secretAccessKey;

    public AwsTemporarySecretToken(@JsonProperty("accessKeyId") String accessKeyId, @JsonProperty("secretAccessKey") String secretAccessKey, @JsonProperty("sessionToken") String sessionToken, @JsonProperty("expiration") long expiration) {
        this.sessionToken = sessionToken;
        this.expiration = expiration;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }
}
