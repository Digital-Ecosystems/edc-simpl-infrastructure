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

package eu.europa.ec.simpl.programme.infrastructure.edc;

import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

public interface InfrastructureSchema {

    String INFRASTRUCTURE_TYPE = "Infrastructure";

    String PROVISIONING_API = "provisioningAPI";
    String PROVISIONING_API_PATH = EDC_NAMESPACE + "provisioningAPI";

    String DEPLOYMENT_SCRIPT_ID = "deploymentScriptId";
    String DEPLOYMENT_SCRIPT_ID_PATH = EDC_NAMESPACE + "deploymentScriptId";

    String SECRET_KEY_PATH = EDC_NAMESPACE + "secretKey";
    String CONSUMER_EMAIL_PATH = EDC_NAMESPACE + "consumerEmail";
}
