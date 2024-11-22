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
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult;
import org.eclipse.edc.connector.dataplane.util.sink.ParallelSink;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

public class InfrastructureDataSink extends ParallelSink {

    private BackendAPIClient backendAPIClient;
    private String consumerEmail;

    InfrastructureDataSink() {}

    @Override
    protected StreamResult<Object> transferParts(List<DataSource.Part> parts) {

        for (DataSource.Part part : parts) {
            var dataSource = (InfrastructureDataSource) part;

            var transferProcessId = dataSource.getTransferProcessId();
            var provisioningAPI = dataSource.getProvisioningAPI();
            var deploymentScriptId = dataSource.getDeploymentScriptId();

            var response = backendAPIClient.sendTriggerRequest(provisioningAPI, deploymentScriptId, transferProcessId, consumerEmail);

            if (response.success()) {
                this.monitor.info(format("Script trigger request successful send to provisioningAPI %s, transferProcessId: %s, deploymentScriptId: %s",
                        provisioningAPI, transferProcessId, deploymentScriptId));
            } else {
                return StreamResult.error((format("Error sending script trigger request to provisioningAPI %s, error: %s",
                        provisioningAPI, response.message())));
            }
        }

        return StreamResult.success();
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

        public Builder consumerEmail(String consumerEmail) {
            sink.consumerEmail = consumerEmail;
            return this;
        }

        @Override
        protected void validate() {
            Objects.requireNonNull(sink.backendAPIClient, "backendAPIClient is required");
            Objects.requireNonNull(sink.consumerEmail, "consumerEmail is required");
        }
    }
}
