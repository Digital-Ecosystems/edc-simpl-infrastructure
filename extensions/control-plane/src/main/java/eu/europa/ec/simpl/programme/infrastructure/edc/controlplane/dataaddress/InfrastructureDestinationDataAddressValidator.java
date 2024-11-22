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

package eu.europa.ec.simpl.programme.infrastructure.edc.controlplane.dataaddress;

import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.util.string.StringUtils;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.edc.validator.spi.Violation.violation;

public class InfrastructureDestinationDataAddressValidator implements Validator<DataAddress> {

    @Override
    public ValidationResult validate(@NotNull DataAddress dataAddress) {

        var consumerEmail = dataAddress.getStringProperty(InfrastructureDataAddressSchema.CONSUMER_EMAIL_PATH);
        if (StringUtils.isNullOrBlank(consumerEmail)) {
            var violation = violation("consumerEmail is required", InfrastructureDataAddressSchema.CONSUMER_EMAIL_PATH);
            return ValidationResult.failure(violation);
        }

        return ValidationResult.success();
    }
}
