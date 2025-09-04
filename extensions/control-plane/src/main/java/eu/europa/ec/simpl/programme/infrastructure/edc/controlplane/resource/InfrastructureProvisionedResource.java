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
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedDataDestinationResource;

import static eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema.*;

public class InfrastructureProvisionedResource extends ProvisionedDataDestinationResource {

    private String secretKey;
    private String consumerEmail;

    private InfrastructureProvisionedResource() {
    }

    public static class Builder
            extends ProvisionedDataDestinationResource.Builder<InfrastructureProvisionedResource, Builder> {

        private Builder() {
            super(new InfrastructureProvisionedResource());
            dataAddressBuilder.type(INFRASTRUCTURE_TYPE);
        }

        public Builder secretKey(String secretKey) {
            dataAddressBuilder.property(SECRET_KEY_PATH, secretKey);
            return this;
        }

        public Builder consumerEmail(String consumerEmail) {
            dataAddressBuilder.property(CONSUMER_EMAIL_PATH, consumerEmail);
            return this;
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }
    }
}
