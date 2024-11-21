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

package eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.datasource;

import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult;
import org.eclipse.edc.spi.EdcException;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult.success;

public class InfrastructureDataSource implements DataSource, DataSource.Part {

    private final String transferProcessId;
    private final String provisioningAPI;
    private final String deploymentScriptId;

    InfrastructureDataSource(String transferProcessId, String provisioningAPI, String deploymentScriptId) {
        this.transferProcessId = transferProcessId;
        this.provisioningAPI = provisioningAPI;
        this.deploymentScriptId = deploymentScriptId;
    }

    public String getTransferProcessId() {
        return transferProcessId;
    }

    public String getProvisioningAPI() {
        return provisioningAPI;
    }

    public String getDeploymentScriptId() {
        return deploymentScriptId;
    }

    @Override
    public StreamResult<Stream<Part>> openPartStream() {
        return success(Stream.of(this));
    }

    @Override
    public String name() {
        return transferProcessId;
    }

    @Override
    public InputStream openStream() {
        throw new EdcException("openStream not supported");
    }

}
