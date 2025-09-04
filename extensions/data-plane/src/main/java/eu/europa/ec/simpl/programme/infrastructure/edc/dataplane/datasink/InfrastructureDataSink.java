/*
 *  Copyright (c) 2024 IONOS
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

package eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.datasink;

import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.backend.BackendAPIClient;
import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.datasource.InfrastructureDataSource;
import eu.europa.ec.simpl.programme.infrastructure.edc.types.AuthToken;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult;
import org.eclipse.edc.connector.dataplane.util.sink.ParallelSink;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

public class InfrastructureDataSink extends ParallelSink {

    private BackendAPIClient backendAPIClient;
    private TypeManager typeManager;
    private Vault vault;
    private String keyName;
    private String defaultAuthToken;
    private String consumerEmail;

    InfrastructureDataSink() {}

    @Override
    protected StreamResult<Object> transferParts(List<DataSource.Part> parts) {

        for (DataSource.Part part : parts) {
            var dataSource = (InfrastructureDataSource) part;

            var requesterUniqueId = String.format("%s.%s", dataSource.getParticipantId(), dataSource.getContractAgreementId());

            var provisioningAPI = dataSource.getProvisioningAPI();
            var deploymentScriptId = dataSource.getDeploymentScriptId();

            var authToken = getAuthToken();

            var response = backendAPIClient.sendTriggerRequest(provisioningAPI, authToken, deploymentScriptId, requesterUniqueId, consumerEmail);

            if (response.success()) {
                this.monitor.info(format("Script trigger request successful send to provisioningAPI %s, requesterUniqueId: %s, deploymentScriptId: %s",
                        provisioningAPI, requesterUniqueId, deploymentScriptId));
            } else {
                return StreamResult.error((format("Error sending script trigger request to provisioningAPI %s, error: %s",
                        provisioningAPI, response.message())));
            }
        }

        return StreamResult.success();
    }

    private String getAuthToken() {
        if (keyName == null) {
            return defaultAuthToken;
        }

        var secret = vault.resolveSecret(keyName);
        if (secret == null) {
            throw new EdcException(format("Secret with key %s not found in Vault", keyName));
        }

        var authToken = typeManager.readValue(secret, AuthToken.class);
        return authToken.getTokenValue();
    }

    public static class Builder extends ParallelSink.Builder<Builder, InfrastructureDataSink> {

        private Builder() {
            super(new InfrastructureDataSink());
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder backendAPIClient(BackendAPIClient backendAPIClient) {
            sink.backendAPIClient = backendAPIClient;
            return this;
        }

        public Builder typeManager(TypeManager typeManager) {
            sink.typeManager = typeManager;
            return this;
        }

        public Builder vault(Vault vault) {
            sink.vault = vault;
            return this;
        }

        public Builder keyName(String keyName) {
            sink.keyName = keyName;
            return this;
        }

        public Builder defaultAuthToken(String defaultAuthToken) {
            sink.defaultAuthToken = defaultAuthToken;
            return this;
        }

        public Builder consumerEmail(String consumerEmail) {
            sink.consumerEmail = consumerEmail;
            return this;
        }

        @Override
        protected void validate() {
            Objects.requireNonNull(sink.backendAPIClient, "backendAPIClient is required");
            Objects.requireNonNull(sink.typeManager, "typeManager is required");
            Objects.requireNonNull(sink.vault, "vault is required");
            Objects.requireNonNull(sink.defaultAuthToken, "defaultAuthToken is required");
            Objects.requireNonNull(sink.consumerEmail, "consumerEmail is required");
        }
    }
}
