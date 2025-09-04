/*
 *  Copyright (c) 2022 IONOS
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      IONOS
 *
 */

package eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;

public class InfrastructureResourceDefinition extends ResourceDefinition {

    private String keyName;
    private String secretKey;
    private String consumerEmail;

    public InfrastructureResourceDefinition() {
    }

    public String getKeyName() {
        return keyName;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getConsumerEmail() {
        return consumerEmail;
    }

    @Override
    public Builder toBuilder() {
        return initializeBuilder(new Builder())
                .keyName(keyName)
                .secretKey(secretKey)
                .consumerEmail(consumerEmail);
    }

    public static class Builder extends ResourceDefinition.Builder<InfrastructureResourceDefinition, Builder> {

        private Builder() {
            super(new InfrastructureResourceDefinition());
        }
        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder keyName(String keyName) {
            resourceDefinition.keyName = keyName;
            return this;
        }

        public Builder secretKey(String secretKey) {
            resourceDefinition.secretKey = secretKey;
            return this;
        }

        public Builder consumerEmail(String consumerEmail) {
            resourceDefinition.consumerEmail = consumerEmail;
            return this;
        }
    }
}
