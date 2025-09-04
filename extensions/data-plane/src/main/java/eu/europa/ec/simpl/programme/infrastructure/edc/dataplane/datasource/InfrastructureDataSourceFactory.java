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

import eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSourceFactory;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.jetbrains.annotations.NotNull;

public class InfrastructureDataSourceFactory implements DataSourceFactory {
    
    private final String participantId;
    private final DataAddressValidatorRegistry dataAddressValidatorRegistry;
    private final TransferProcessStore transferProcessStore;
    private final AssetIndex assetIndex;
    
    public InfrastructureDataSourceFactory(String participantId,
                                           DataAddressValidatorRegistry dataAddressValidatorRegistry,
                                           TransferProcessStore transferProcessStore,
                                           AssetIndex assetIndex) {
        this.participantId = participantId;
        this.dataAddressValidatorRegistry = dataAddressValidatorRegistry;
        this.transferProcessStore = transferProcessStore;
        this.assetIndex = assetIndex;
    }

    @Override
    public String supportedType() {
        return InfrastructureSchema.INFRASTRUCTURE_TYPE;
    }

    @Override
    public @NotNull Result<Void> validateRequest(DataFlowStartMessage request) {
        var source = request.getSourceDataAddress();
        return dataAddressValidatorRegistry.validateSource(source).flatMap(ValidationResult::toResult);
    }

    @Override
    public DataSource createSource(DataFlowStartMessage request) {

        var transferProcessId = request.getProcessId();

        var transferProcess = transferProcessStore.findById(transferProcessId);
        if (transferProcess == null) {
            throw new EdcException("Transfer process not found: " + transferProcessId);
        }
        var assetId = transferProcess.getAssetId();

        var asset = assetIndex.findById(assetId);
        if (asset == null) {
            throw new EdcException("Asset not found: " + assetId);
        }
        if (!asset.getDataAddress().getType().equals(InfrastructureSchema.INFRASTRUCTURE_TYPE)) {
            throw new EdcException("Invalid asset data address type: " + asset.getDataAddress().getType());
        }

        var contractAgreementId = transferProcess.getContractId();

        var provisioningAPI = asset.getDataAddress().getStringProperty(InfrastructureSchema.PROVISIONING_API);
        var deploymentScriptId = asset.getDataAddress().getStringProperty(InfrastructureSchema.DEPLOYMENT_SCRIPT_ID);

        return new InfrastructureDataSource(participantId, contractAgreementId, provisioningAPI, deploymentScriptId);
    }

}
