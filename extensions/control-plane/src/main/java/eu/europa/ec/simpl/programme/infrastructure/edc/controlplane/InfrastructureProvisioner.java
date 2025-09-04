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

package eu.europa.ec.simpl.programme.infrastructure.edc.controlplane;

import eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.resource.InfrastructureProvisionedResource;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.resource.InfrastructureResourceDefinition;
import eu.europa.ec.simpl.programme.infrastructure.edc.types.AuthToken;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.Provisioner;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.DeprovisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionResponse;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.security.Vault;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;

public class InfrastructureProvisioner implements Provisioner<InfrastructureResourceDefinition, InfrastructureProvisionedResource> {

    private final Monitor monitor;
    private final Vault vault;

    public InfrastructureProvisioner(Monitor monitor, Vault vault) {
        this.monitor = monitor;
        this.vault = vault;
    }

    @Override
    public boolean canProvision(ResourceDefinition resourceDefinition) {
        return resourceDefinition instanceof InfrastructureResourceDefinition;
    }

    @Override
    public boolean canDeprovision(ProvisionedResource resourceDefinition) {
        return resourceDefinition instanceof InfrastructureProvisionedResource;
    }

    @Override
    public CompletableFuture<StatusResult<ProvisionResponse>> provision(InfrastructureResourceDefinition resourceDefinition, Policy policy) {

        String consumerEmail = resourceDefinition.getConsumerEmail();

        var resourceName = resourceDefinition.getKeyName();
        var resourceBuilder = InfrastructureProvisionedResource.Builder.newInstance()
                .id(resourceDefinition.getId())
                .resourceName(resourceName)
                .resourceDefinitionId(resourceDefinition.getId())
                .transferProcessId(resourceDefinition.getTransferProcessId())
                .consumerEmail(consumerEmail);

        String secretKey = resourceDefinition.getSecretKey();
        if (secretKey != null) {
            resourceBuilder = resourceBuilder
                    .secretKey(secretKey)
                    .hasToken(true);
        }

        var resource = resourceBuilder.build();

        var responseBuilder = ProvisionResponse.Builder.newInstance()
                .resource(resource);

        if (secretKey != null) {
            var secret = getSecret(secretKey);
            var expiryTime = OffsetDateTime.now().plusMinutes(5);
            var secretToken = new AuthToken(secret, expiryTime.toInstant().toEpochMilli());

            responseBuilder = responseBuilder.secretToken(secretToken);
        }

        var response = responseBuilder.build();
        return CompletableFuture.completedFuture(StatusResult.success(response));
    }

    private String getSecret(String secretKey) {
        var secret = vault.resolveSecret(secretKey);
        if (secret == null) {
            throw new EdcException(format("Secret with key %s not found in Vault", secretKey));
        }
        return secret;
    }

    @Override
    public CompletableFuture<StatusResult<DeprovisionedResource>> deprovision(InfrastructureProvisionedResource provisionedResource, Policy policy) {
        var dataAddress = provisionedResource.getDataAddress();
        var secretKey = dataAddress.getStringProperty(InfrastructureSchema.SECRET_KEY_PATH);

        if (secretKey != null) {
            var result = vault.deleteSecret(secretKey);
            if (result.failed()) {
                this.monitor.warning(format("Error deleting secret with key %s from Vault", secretKey));
            }
        }

        var resource = DeprovisionedResource.Builder.newInstance().provisionedResourceId(provisionedResource.getId()).build();
        return CompletableFuture.completedFuture(StatusResult.success(resource));

    }
}