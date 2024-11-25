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
val extensionVersion: String? = project.findProperty("extensionVersion") as String? ?: System.getenv("EXTENSION_VERSION")

val edcGroup: String by project
val edcVersion: String by project
val junitVersion: String by project

val gitHubUser: String? = project.findProperty("github.user") as String? ?: System.getenv("GITHUB_USER")
val gitHubToken: String? = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")

dependencies {
	implementation("${edcGroup}:connector-core:${edcVersion}")
	implementation("${edcGroup}:control-plane-spi:${edcVersion}")
	implementation("${edcGroup}:data-plane-spi:${edcVersion}")
	implementation("${edcGroup}:data-plane-util:${edcVersion}")
	implementation("${edcGroup}:http-lib:${edcVersion}")

	implementation(project(":extensions:control-plane"))

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
			artifactId = "data-plane"
			version = extensionVersion

			from(components["java"])

			pom {
				name.set("data-plane")
				description.set("SIMPL Infrastructure Data Plane extensions")
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
