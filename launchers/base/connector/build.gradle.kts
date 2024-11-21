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

plugins {
    `java-library`
}

val edcGroup: String by project
val edcVersion: String by project

dependencies {
	// Core
	implementation("${edcGroup}:connector-core:${edcVersion}")
	implementation("${edcGroup}:http:${edcVersion}")
	implementation("${edcGroup}:dsp:${edcVersion}")
	implementation("${edcGroup}:management-api:${edcVersion}")

	// Control Plane
	implementation("${edcGroup}:control-plane-core:${edcVersion}")
	implementation("${edcGroup}:control-plane-api-client:${edcVersion}")
	implementation("${edcGroup}:control-plane-api:${edcVersion}")
	implementation("${edcGroup}:control-api-configuration:${edcVersion}")

	// Data Plane
	implementation("${edcGroup}:data-plane-core:${edcVersion}")
	implementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
	implementation("${edcGroup}:data-plane-selector-api:${edcVersion}")
	implementation("${edcGroup}:data-plane-self-registration:${edcVersion}")
	implementation("${edcGroup}:edr-store-core:${edcVersion}")
	implementation("${edcGroup}:transfer-data-plane-signaling:${edcVersion}")
}

tasks.jar {
	archiveFileName.set("base-connector.jar")
}
