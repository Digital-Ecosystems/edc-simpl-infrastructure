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
	`maven-publish`
}

val extensionGroup: String by project

val edcGroup: String by project
val edcVersion: String by project
val junitVersion: String by project

val version: String? = System.getenv("VERSION")
val gitHubUser: String? = project.findProperty("github.user") as String? ?: System.getenv("GITHUB_USER")
val gitHubToken: String? = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")

dependencies {
	implementation("${edcGroup}:connector-core:${edcVersion}")
	implementation("${edcGroup}:control-plane-spi:${edcVersion}")
	implementation("${edcGroup}:util-lib:${edcVersion}")

	testImplementation("${edcGroup}:junit:${edcVersion}")
	testImplementation("${edcGroup}:http-spi:${edcVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

java {
	withJavadocJar()
	withSourcesJar()
}

tasks.test {
	useJUnitPlatform()
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = extensionGroup
			artifactId = "control-plane"
			version = version

			from(components["java"])

			pom {
				name.set("control-plane")
				description.set("SIMPL Infrastructure Control Plane Extensions")
			}
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/Digital-Ecosystems/edc-simpl-infrastructure")

			credentials {
				username = gitHubUser
				password = gitHubToken
			}
		}
	}
}
