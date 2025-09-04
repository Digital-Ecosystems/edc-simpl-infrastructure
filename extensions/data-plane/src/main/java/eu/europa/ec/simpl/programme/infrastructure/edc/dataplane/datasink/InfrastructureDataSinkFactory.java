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

import eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema;
import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.backend.BackendAPIClient;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSink;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSinkFactory;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

import static eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema.CONSUMER_EMAIL_PATH;

public class InfrastructureDataSinkFactory implements DataSinkFactory {

    private final Monitor monitor;
    private final DataAddressValidatorRegistry dataAddressValidatorRegistry;
    private final ExecutorService executorService;
    private final Vault vault;
    private final BackendAPIClient backendAPIClient;
    private final TypeManager typeManager;
    private final String defaultAuthToken;

    public InfrastructureDataSinkFactory(Monitor monitor,
                                         DataAddressValidatorRegistry dataAddressValidatorRegistry,
                                         ExecutorService executorService,
                                         Vault vault,
                                         BackendAPIClient backendAPIClient,
                                         TypeManager typeManager,
                                         String defaultAuthToken) {
        this.monitor = monitor;
        this.dataAddressValidatorRegistry = dataAddressValidatorRegistry;
        this.executorService = executorService;
        this.vault = vault;
        this.backendAPIClient = backendAPIClient;
        this.typeManager = typeManager;
        this.defaultAuthToken = defaultAuthToken;
    }

    @Override
    public String supportedType() {
        return InfrastructureSchema.INFRASTRUCTURE_TYPE;
    }

    @Override
    public @NotNull Result<Void> validateRequest(DataFlowStartMessage request) {
        var destination = request.getDestinationDataAddress();
        return dataAddressValidatorRegistry.validateDestination(destination).flatMap(ValidationResult::toResult);
    }

    @Override
    public DataSink createSink(DataFlowStartMessage request) {
        var destination = request.getDestinationDataAddress();
        var keyName = destination.getKeyName();
        var consumerEmail = destination.getStringProperty(CONSUMER_EMAIL_PATH);

        return InfrastructureDataSink.Builder.newInstance()
                .monitor(monitor)
                .executorService(executorService)
                .requestId(request.getId())
                .backendAPIClient(backendAPIClient)
                .typeManager(typeManager)
                .vault(vault)
                .keyName(keyName)
                .defaultAuthToken(defaultAuthToken)
                .consumerEmail(consumerEmail)
                .build();
    }
}

