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

package eu.europa.ec.simpl.programme.infrastructure.edc.dataplane;

import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.backend.BackendAPIClient;
import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.datasink.InfrastructureDataSinkFactory;
import eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.datasource.InfrastructureDataSourceFactory;
import okhttp3.OkHttpClient;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataTransferExecutorServiceContainer;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;

@Provides(SimplDataPlaneExtension.class)
@Extension(value = SimplDataPlaneExtension.NAME)
public class SimplDataPlaneExtension implements ServiceExtension {

    public static final String NAME = "SIMPL Infrastructure Data Plane Extensions";

    @Inject
    private DataAddressValidatorRegistry dataAddressValidatorRegistry;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private DataTransferExecutorServiceContainer executorContainer;

    @Inject
    private TypeManager typeManager;

    @Inject
    private TransferProcessStore transferProcessStore;

    @Inject
    private AssetIndex assetIndex;

    @Inject
    private OkHttpClient httpClient;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor().withPrefix(this.getClass().getSimpleName());

        // Backend API
        var backendAPIClient = new BackendAPIClient(httpClient, typeManager.getMapper());

        // Data Plane
        var participantId = context.getParticipantId();
        var sourceFactory = new InfrastructureDataSourceFactory(participantId, dataAddressValidatorRegistry, transferProcessStore, assetIndex);
        pipelineService.registerFactory(sourceFactory);

        var sinkFactory = new InfrastructureDataSinkFactory(monitor, dataAddressValidatorRegistry, executorContainer.getExecutorService(), backendAPIClient);
        pipelineService.registerFactory(sinkFactory);
        monitor.debug("Data Plane components initialized");

        monitor.info("Extension initialized");
    }
}
