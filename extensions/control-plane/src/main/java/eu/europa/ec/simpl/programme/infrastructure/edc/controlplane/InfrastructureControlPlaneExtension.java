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

package eu.europa.ec.simpl.programme.infrastructure.edc.controlplane;

import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.resource.InfrastructureConsumerResourceDefinitionGenerator;
import eu.europa.ec.simpl.programme.infrastructure.edc.InfrastructureSchema;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress.InfrastructureDestinationDataAddressValidator;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress.InfrastructureSourceDataAddressValidator;
import eu.europa.ec.simpl.programme.infrastructure.edc.types.AuthToken;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ProvisionManager;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ResourceManifestGenerator;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;

@Provides(InfrastructureControlPlaneExtension.class)
@Extension(value = InfrastructureControlPlaneExtension.NAME)
public class InfrastructureControlPlaneExtension implements ServiceExtension {

    public static final String NAME = "SIMPL Infrastructure Control Plane Extensions";

    @Inject
    private DataAddressValidatorRegistry dataAddressValidatorRegistry;

    @Inject
    private ResourceManifestGenerator manifestGenerator;

    @Inject
    private ProvisionManager provisionManager;

    @Inject
    private TypeManager typeManager;

    @Inject
    private Vault vault;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor().withPrefix(this.getClass().getSimpleName());

        // Provisioner
        var provisioner = new InfrastructureProvisioner(monitor, vault);
        provisionManager.register(provisioner);
        manifestGenerator.registerGenerator(new InfrastructureConsumerResourceDefinitionGenerator());

        // Types
        typeManager.registerTypes(AuthToken.class);

        // Validators
        var sourceValidator = new InfrastructureSourceDataAddressValidator();
        dataAddressValidatorRegistry.registerSourceValidator(InfrastructureSchema.INFRASTRUCTURE_TYPE, sourceValidator);

        var destinationValidator = new InfrastructureDestinationDataAddressValidator();
        dataAddressValidatorRegistry.registerDestinationValidator(InfrastructureSchema.INFRASTRUCTURE_TYPE, destinationValidator);
        monitor.debug("Validators initialized");

        monitor.info("Extension initialized");
    }
}
