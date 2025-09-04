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

import eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ConsumerResourceDefinitionGenerator;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;

import org.jetbrains.annotations.Nullable;

public class InfrastructureConsumerResourceDefinitionGenerator implements ConsumerResourceDefinitionGenerator {

    @Override
    public @Nullable ResourceDefinition generate(TransferProcess transferProcess, Policy policy) {
        var destination = transferProcess.getDataDestination();
        var keyName = transferProcess.getId();
        var secretKey = destination.getStringProperty(InfrastructureSchema.SECRET_KEY_PATH);
        var consumerEmail = destination.getStringProperty(InfrastructureSchema.CONSUMER_EMAIL_PATH);

        return InfrastructureResourceDefinition.Builder.newInstance()
                .id(transferProcess.getId())
                .keyName(keyName)
                .secretKey(secretKey)
                .consumerEmail(consumerEmail)
                .build();
    }

    @Override
    public boolean canGenerate(TransferProcess transferProcess, Policy policy) {
        var dataDestination = transferProcess.getDataDestination();
        return dataDestination.getType().equals(InfrastructureSchema.INFRASTRUCTURE_TYPE);
    }

}
