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

import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress.InfrastructureDataAddressSchema;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress.InfrastructureDestinationDataAddressValidator;
import eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress.InfrastructureSourceDataAddressValidator;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;

@Provides(SimplControlPlaneExtension.class)
@Extension(value = SimplControlPlaneExtension.NAME)
public class SimplControlPlaneExtension implements ServiceExtension {

    public static final String NAME = "SIMPL Infrastructure Control Plane Extensions";

    @Inject
    private DataAddressValidatorRegistry dataAddressValidatorRegistry;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor().withPrefix(this.getClass().getSimpleName());

        // Validators
        var sourceValidator = new InfrastructureSourceDataAddressValidator();
        dataAddressValidatorRegistry.registerSourceValidator(InfrastructureDataAddressSchema.INFRASTRUCTURE_TYPE, sourceValidator);

        var destinationValidator = new InfrastructureDestinationDataAddressValidator();
        dataAddressValidatorRegistry.registerDestinationValidator(InfrastructureDataAddressSchema.INFRASTRUCTURE_TYPE, destinationValidator);
        monitor.debug("Validators initialized");

        monitor.info("Extension initialized");
    }
}
